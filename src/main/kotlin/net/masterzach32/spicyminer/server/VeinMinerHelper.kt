package net.masterzach32.spicyminer.server

import com.spicymemes.core.util.BlockInWorld
import com.spicymemes.core.util.getBlock
import com.spicymemes.core.util.getBlocksWithin
import net.masterzach32.spicyminer.util.DropSet
import net.minecraft.block.Block
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Main logic
 */
object VeinMinerHelper {

    private const val MAX_BLOCKS = 50

    private val tools = mutableListOf<Item>()

    init {
        tools.addAll(
                listOf(
                        Items.DIAMOND_PICKAXE,
                        Items.GOLDEN_PICKAXE,
                        Items.IRON_PICKAXE,
                        Items.STONE_PICKAXE,
                        Items.WOODEN_PICKAXE
                )
        )
    }

    // TODO: add other mods' tools
    fun isValidTool(item: Item) = tools.contains(item)

    fun isValidTool(stack: ItemStack) = isValidTool(stack.item)

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
            biw.block.dropXpOnBlockBreak(biw.world, biw.pos, biw.block.getExpDrop(biw.state, biw.world, biw.pos, fortune))
        }
        drops.getDrops().forEach { Block.spawnAsEntity(biw.world, biw.pos, it) }
    }
}