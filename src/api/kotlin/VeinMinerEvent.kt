package com.spicymemes.veinminer.api

import net.minecraft.core.*
import net.minecraft.world.entity.player.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.state.*
import net.minecraftforge.eventbus.api.*

abstract class VeinMinerEvent(
    val pos: BlockPos,
    val state: BlockState,
    val player: Player,
    val tool: ItemStack
) : Event() {

    /**
     * Event to check if veinmine should continue. Set allowContinue to false to cancel.
     */
    class PreToolUseCheck(
            pos: BlockPos,
            state: BlockState,
            player: Player,
            tool: ItemStack,
            var allowContinue: Boolean = true
    ) : VeinMinerEvent(pos, state, player, tool)

    /**
     * Event fired after successful veinmine.
     */
    class PostToolUse(
            pos: BlockPos,
            state: BlockState,
            player: Player,
            tool: ItemStack,
            val minerDataPre: MinerData
    ) : VeinMinerEvent(pos, state, player, tool)
}