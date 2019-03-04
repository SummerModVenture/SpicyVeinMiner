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
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 * Main logic
 */
object VeinMinerHelper {

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
            harvestBlocks(biw.pos, biw.world, biw.block, biw.state, tool, player, getAlikeBlocks(biw))
            MinecraftForge.EVENT_BUS.post(VeinMinerEvent.PostToolUse(biw.pos, biw.state, player, tool))
        }
    }

    fun isValidTool(stack: ItemStack) = Config.registeredTools.any { it.value.contains(stack.item.registryName) }

    fun isValidBlock(block: Block) = !Config.blockBlacklist.contains(block.registryName)

    private fun getAlikeBlocks(biw: BlockInWorld) = getAlikeBlocks(biw.pos, biw.pos, biw.world, biw.block, mutableSetOf()).toList()

    private fun getAlikeBlocks(
            origin: BlockPos,
            pos: BlockPos,
            world: World,
            block: Block,
            blocks: MutableSet<BlockPos>
    ): Set<BlockPos> {
        pos.getBlocksWithin(1).asSequence()
                .filter { world.getBlock(it).block.unlocalizedName == block.unlocalizedName }
                .filter { origin.distance(it) < range }
                .filter { blocks.size <= limit && blocks.add(it) }
                .forEach { getAlikeBlocks(origin, it, world, block, blocks) }

        return blocks
    }

    private fun harvestBlocks(
            origin: BlockPos,
            world: World,
            block: Block,
            state: IBlockState,
            tool: ItemStack,
            player: EntityPlayer,
            blocks: List<BlockPos>
    ) {
        if (blocks.isEmpty())
            return

        val drops = DropSet()
        blocks.forEach {
            world.setBlockToAir(it)

            val list = NonNullList.create<ItemStack>()
            val fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            block.getDrops(list, world, it, state, fortune)
            list.forEach { stack -> drops.addDrop(stack) }

            tool.damageItem(1, player)
            player.addExhaustion(exhaustion.toFloat())
            block.dropXpOnBlockBreak(world, origin, block.getExpDrop(state, world, it, fortune))
        }
        drops.getDrops().forEach { Block.spawnAsEntity(world, origin, it) }
    }
}