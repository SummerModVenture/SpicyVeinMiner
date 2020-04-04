package com.spicymemes.veinminer.server

import com.spicymemes.core.extensions.*
import com.spicymemes.core.util.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.api.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.util.*
import net.minecraft.block.*
import net.minecraft.enchantment.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.*
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
        if (event.world.isRemote)
            throw IllegalStateException("VeinMinerHelper#attemptExcavate() must be run on the server!")
        val world = event.world.toServerWorld()
        val player = event.player.toServerPlayerEntity()
        val tool = event.player.heldItemMainhand ?: return

        val preCheckEvent = VeinMinerEvent.PreToolUseCheck(event.pos, event.state, event.player, tool)
        MinecraftForge.EVENT_BUS.post(preCheckEvent)
        if (
                isValidTool(tool) &&
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
                            getAlikeBlocks(event.pos, world, event.state.block, player.minerData.blockLimit)
                    )
            )
        }
    }

    fun isValidTool(stack: ItemStack) = Config.registeredTools.any { it.value.contains(stack.item.registryName) }

    fun isValidBlock(block: Block) = Config.registeredBlocks.contains(block.registryName)

    fun processLimitedBlocks(limit: Int) {
        var i = 0
        while (instances.peek() != null && i < limit) {
            val instance = instances.peek()
            if (!instance.isFinished)
                while (instance.harvestNextBlock() && i < limit) { i++ }
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

    private fun getAlikeBlocks(originPos: BlockPos, world: World, block: Block, playerLimit: Int): Set<BlockPos> {
        return mutableSetOf<BlockPos>().also { getAlikeBlocks(originPos, originPos, world, block, it, playerLimit) }
    }

    private fun getAlikeBlocks(
            origin: BlockPos,
            pos: BlockPos,
            world: World,
            block: Block,
            blocks: MutableSet<BlockPos>,
            playerLimit: Int
    ) {
        pos.getBlocksWithinMutable(1)
                .filter { world.getBlock(it).block.registryName == block.registryName }
                .filter { origin.distance(it) < range }
                .filter { blocks.size <= playerLimit && blocks.add(it.toImmutable()) }
                .forEach { getAlikeBlocks(origin, it, world, block, blocks, playerLimit) }
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

        val isFinished get() = queue.peek() == null || tool.isEmpty

        private var count = 0

        init {
            logger.debug("Attempting veinmine at $originPos with ${queue.size} blocks.")
        }

        fun harvestNextBlock(): Boolean {
            val nextPos = queue.poll()

            Block.getDrops(state, world, nextPos, world.getTileEntity(nextPos), player, tool)
                    .forEach { stack -> drops.addDrop(stack) }

            world.setBlockState(nextPos, Blocks.AIR.defaultState)

            tool.damageItem(1, player, { it.sendBreakAnimation(player.swingingHand) })
            player.addExhaustion(exhaustion.toFloat())
            val fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            state.block.dropXpOnBlockBreak(world, originPos, state.block.getExpDrop(state, world, nextPos, fortune, 0))
            count++
            return !isFinished
        }

        fun cleanup() {
            if (!isFinished)
                throw IllegalStateException("VeinMinerInstance#cleanup() was called early!")
            drops.getDrops().forEach { Block.spawnAsEntity(world, originPos, it) }
            MinecraftForge.EVENT_BUS.post(VeinMinerEvent.PostToolUse(originPos, state, player, tool, player.minerData))
            player.addBlocksMined(count+1)
        }
    }
}