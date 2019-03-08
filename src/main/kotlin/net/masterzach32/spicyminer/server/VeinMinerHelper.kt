package net.masterzach32.spicyminer.server

import com.spicymemes.core.util.BlockInWorld
import com.spicymemes.core.util.distance
import com.spicymemes.core.util.getBlock
import com.spicymemes.core.util.getBlocksWithin
import net.masterzach32.spicyminer.api.VeinMinerEvent
import net.masterzach32.spicyminer.config.*
import net.masterzach32.spicyminer.util.DropSet
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Main logic
 */
object VeinMinerHelper {

    private val instances = ConcurrentLinkedQueue<VeinMinerInstance>()

    fun attemptExcavate(biw: BlockInWorld, tool: ItemStack?, player: EntityPlayerMP) {
        if (biw.world.isRemote)
            throw Exception("VeinMinerHelper#attemptExcavate() must be run on the server!")
        if (tool == null)
            return

        val preCheckEvent = VeinMinerEvent.PreToolUseCheck(biw.pos, biw.state, player, tool)
        MinecraftForge.EVENT_BUS.post(preCheckEvent)
        if (
                isValidTool(tool) &&
                isValidBlock(biw.block) &&
                preCheckEvent.allowContinue
        ) {
            instances.add(VeinMinerInstance(biw.pos, biw.world, biw.state, tool, player, getAlikeBlocks(biw)))
        }
    }

    fun isValidTool(stack: ItemStack) = Config.registeredTools.any { it.value.contains(stack.item.registryName) }

    fun isValidBlock(block: Block) = !Config.blockBlacklist.contains(block.registryName)

    fun processLimitedBlocks(limit: Int) {
        var i = 0
        while (instances.peek() != null && i < limit) {
            val instance = instances.peek()
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
            while (instance.harvestNextBlock()) {}
            instance.cleanup()
        }
    }

    private fun getAlikeBlocks(biw: BlockInWorld): Set<BlockPos> {
        return mutableSetOf<BlockPos>().also { getAlikeBlocks(biw.pos, biw.pos, biw.world, biw.block, it) }
    }

    private fun getAlikeBlocks(
            origin: BlockPos,
            pos: BlockPos,
            world: World,
            block: Block,
            blocks: MutableSet<BlockPos>
    ) {
        pos.getBlocksWithin(1).asSequence()
                .filter { world.getBlock(it).block.unlocalizedName == block.unlocalizedName }
                .filter { origin.distance(it) < range }
                .filter { blocks.size <= limit && blocks.add(it) }
                .forEach { getAlikeBlocks(origin, it, world, block, blocks) }
    }

    class VeinMinerInstance(
            private val origin: BlockPos,
            private val world: World,
            private val state: IBlockState,
            private val tool: ItemStack,
            private val player: EntityPlayerMP,
            blocks: Set<BlockPos>
    ) {

        private val queue = ConcurrentLinkedQueue<BlockPos>(blocks)
        private val drops = DropSet()

        val isFinished get() = queue.peek() == null || tool.isEmpty

        fun harvestNextBlock(): Boolean {
            val nextPos = queue.poll()
            world.setBlockToAir(nextPos)

            val list = NonNullList.create<ItemStack>()
            val fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            state.block.getDrops(list, world, nextPos, state, fortune)
            list.forEach { stack -> drops.addDrop(stack) }

            tool.damageItem(1, player)
            player.addExhaustion(exhaustion.toFloat())
            state.block.dropXpOnBlockBreak(world, origin, state.block.getExpDrop(state, world, nextPos, fortune))
            return !isFinished
        }

        fun cleanup() {
            if (!isFinished)
                throw IllegalStateException("VeinMinerInstance#cleanup() was called early!")
            drops.getDrops().forEach { Block.spawnAsEntity(world, origin, it) }
            MinecraftForge.EVENT_BUS.post(VeinMinerEvent.PostToolUse(origin, state, player, tool))
        }
    }
}