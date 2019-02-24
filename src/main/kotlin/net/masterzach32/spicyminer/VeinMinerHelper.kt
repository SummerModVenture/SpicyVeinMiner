package net.masterzach32.spicyminer

import com.spicymemes.core.util.BlockInWorld
import com.spicymemes.core.util.getBlock
import com.spicymemes.core.util.getBlocksWithin
import net.masterzach32.spicyminer.util.DropSet
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Main logic
 */
object VeinMinerHelper {

    private const val MAX_BLOCKS = 50

    fun getAlikeBlocks(biw: BlockInWorld) = getAlikeBlocks(biw.pos, biw.world, biw.block, mutableSetOf()).toList()

    private fun getAlikeBlocks(pos: BlockPos, world: World, block: Block, blocks: MutableSet<BlockPos>): Set<BlockPos> {
        if (blocks.size < MAX_BLOCKS)
            pos.getBlocksWithin(1).asSequence()
                    .filter { world.getBlock(it).block.unlocalizedName == block.unlocalizedName }
                    .filter { blocks.add(it) }
                    .forEach { getAlikeBlocks(it, world, block, blocks) }

        return blocks
    }

    fun harvestBlocks(biw: BlockInWorld, player: EntityPlayer, tool: ItemStack, blocks: List<BlockPos>) {
        if (blocks.isEmpty())
            return

        val drops = DropSet()
        blocks.forEach {
            biw.world.setBlockToAir(it)

            val list = NonNullList.create<ItemStack>()
            val fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            biw.block.getDrops(list, biw.world, biw.pos, biw.state, fortune)
            list.forEach { stack -> drops.addDrop(stack) }

            tool.damageItem(1, player)
            player.addExhaustion(0.005F)
        }
        drops.getDrops().forEach { Block.spawnAsEntity(biw.world, biw.pos, it) }
    }
}