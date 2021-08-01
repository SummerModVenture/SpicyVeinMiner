package com.spicymemes.veinminer.api

import net.minecraft.world.item.*

class DropSet(private val items: MutableSet<ItemStack> = mutableSetOf()) : Set<ItemStack> by items {

    fun addDrop(stack: ItemStack) {
        items
            .filter { it.item == stack.item && it.isStackable }
            .filter { it.count + stack.count <= it.maxStackSize }
            .let {
                if (it.isEmpty())
                    items.add(stack)
                else
                    it.first().grow(stack.count)
            }
    }
}