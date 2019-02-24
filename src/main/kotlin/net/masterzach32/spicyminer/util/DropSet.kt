package net.masterzach32.spicyminer.util

import net.minecraft.item.ItemStack

class DropSet {

    private val drops = mutableListOf<ItemStack>()

    fun addDrop(stack: ItemStack) {
        drops.asSequence()
                .filter { it.item == stack.item }
                .filter { it.metadata == stack.metadata }
                .filter { it.isStackable }
                .filter { it.count + stack.count <= it.maxStackSize }
                .toList()
                .let {
                    if (it.isEmpty())
                        drops.add(stack)
                    else
                        it.first().grow(stack.count)
                }
    }

    fun getDrops(): List<ItemStack> = drops
}