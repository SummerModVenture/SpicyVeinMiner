package com.spicymemes.veinminer.server

import com.spicymemes.core.extensions.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.api.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.util.*
import net.minecraft.block.*
import net.minecraft.enchantment.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.math.*
import net.minecraft.world.*
import net.minecraft.world.server.*
import net.minecraftforge.common.*
import net.minecraftforge.event.world.*
import java.util.concurrent.*

/**
 * Main logic
 */
object VeinMinerHelper {

    private val instances = ConcurrentLinkedQueue<VeinMinerInstance>()

    fun attemptExcavate(event: BlockEvent.BreakEvent) {
        if (event.world.isClientSide)
            error("VeinMinerHelper#attemptExcavate() must be run on the server!")
        val world = event.world.asServerWorld()
        val player = event.player.asServerPlayerEntity()
        val tool = event.player.mainHandItem ?: return

        val preCheckEvent = VeinMinerEvent.PreToolUseCheck(event.pos, event.state, event.player, tool)
        MinecraftForge.EVENT_BUS.post(preCheckEvent)
        if (isValidTool(tool) &&
                isValidBlock(event.state.block) &&
                preCheckEvent.allowContinue &&
                !preCheckEvent.isCanceled
        ) {
            event.isCanceled = true
            instances.add(
                    VeinMinerInstance(
                            event.pos,
                            event.state,
                            world,
                            player,
                            tool,
                            getAlikeBlocks(
                                    event.pos,
                                    world,
                                    event.state.block,
                                    player.minerData.blockLimit,
                                    ServerConfig.range.get()
                            )
                    )
            )
        }
    }

    fun isValidTool(stack: ItemStack) = ServerConfig.ignoreTools.get() ||
            ServerConfig.allowedTools.get().contains(stack.item.registryName.toString()) ||
            stack.item is PickaxeItem ||
            stack.item is AxeItem ||
            stack.item is ShovelItem

    fun isValidBlock(block: Block) = ServerConfig.allowedBlocks.get().contains(block.registryName.toString()) ||
            block.registryName?.path?.let {
                it.contains("ore") || it.contains("log")
            } ?: false

    private fun getAlikeBlocks(originPos: BlockPos, world: World, block: Block, playerLimit: Int, range: Int): Set<BlockPos> {
        return mutableSetOf<BlockPos>().also { getAlikeBlocks(originPos, originPos, world, block, it, playerLimit, range) }
    }

    private fun getAlikeBlocks(
        origin: BlockPos,
        pos: BlockPos,
        world: World,
        block: Block,
        blocks: MutableSet<BlockPos>,
        playerLimit: Int,
        range: Int
    ) {
        pos.getBlocksWithinMutable(1)
            .filter { world.getBlock(it).block.registryName == block.registryName }
            .filter { origin.distance(it) < range && blocks.size <= playerLimit && blocks.add(it.immutable()) }
            .forEach { getAlikeBlocks(origin, it, world, block, blocks, playerLimit, range) }
    }

    fun processLimitedBlocks(limit: Int) {
        var i = 0
        while (instances.peek() != null && i < limit) {
            val instance = instances.peek()
            if (!instance.isFinished)
                while (instance.harvestNextBlock() && i < limit)
                    i++
            if (instance.isFinished) {
                instance.cleanup()
                instances.remove()
            }
        }
    }

    fun processAllBlocks() {
        while (instances.peek() != null) {
            val instance = instances.poll()
            if (!instance.isFinished)
                while (instance.harvestNextBlock()) {}
            instance.cleanup()
        }
    }

    class VeinMinerInstance(
            private val originPos: BlockPos,
            private val state: BlockState,
            private val world: ServerWorld,
            private val player: ServerPlayerEntity,
            private val tool: ItemStack,
            blocks: Set<BlockPos>
    ) {

        private val queue = ConcurrentLinkedQueue(blocks)
        private val drops = DropSet()

        private val wasToolUse = !tool.isEmpty
        private val exhaustion = ServerConfig.exhaustion.get() *
                if (wasToolUse) 1.0 else ServerConfig.exhaustionMultiplier.get()

        val isFinished get() = queue.peek() == null || (wasToolUse && tool.isEmpty)

        private var count = 0

        init {
            logger.debug("Attempting veinmine at $originPos with ${queue.size} blocks.")
        }

        /**
         * harvests the next block. returns true if there are more blocks to mine, false if this instance is complete
         */
        fun harvestNextBlock(): Boolean {
            val nextPos = queue.poll()

            Block.getDrops(state, world, nextPos, world.getBlockEntity(nextPos), player, tool)
                    .forEach { stack -> drops.addDrop(stack) }

            world.setBlockAndUpdate(nextPos, Blocks.AIR.defaultBlockState())

            tool.hurtAndBreak(1, player) { it.broadcastBreakEvent(player.swingingArm) }
            player.causeFoodExhaustion(exhaustion.toFloat())
            val fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool)
            state.block.popExperience(world, originPos, state.block.getExpDrop(state, world, nextPos, fortune, 0))
            count++
            return !isFinished
        }

        fun cleanup() {
            if (!isFinished)
                error("VeinMinerInstance#cleanup() was called early!")
            drops.getDrops().forEach { Block.popResource(world, originPos, it) }
            MinecraftForge.EVENT_BUS.post(VeinMinerEvent.PostToolUse(originPos, state, player, tool, player.minerData))
            player.addBlocksMined(count + 1)
        }
    }
}