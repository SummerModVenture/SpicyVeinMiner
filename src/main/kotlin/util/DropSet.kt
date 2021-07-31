package com.spicymemes.veinminer.util

import net.minecraft.world.item.*

class DropSet {

    private val drops = mutableListOf<ItemStack>()

    fun addDrop(stack: ItemStack) {
        drops
            .filter { it.item == stack.item && it.isStackable }
            .filter { it.count + stack.count <= it.maxStackSize }
            .let {
                if (it.isEmpty())
                    drops.add(stack)
                else
                    it.first().grow(stack.count)
            }
    }

    fun getDrops(): List<ItemStack> = drops
}