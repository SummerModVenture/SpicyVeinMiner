package com.spicymemes.veinminer.api

import com.spicymemes.veinminer.server.*
import net.minecraft.block.*
import net.minecraft.entity.player.*
import net.minecraft.item.*
import net.minecraft.util.math.*
import net.minecraftforge.eventbus.api.*

abstract class VeinMinerEvent(
        val pos: BlockPos,
        val state: BlockState,
        val player: PlayerEntity,
        val tool: ItemStack
) : Event() {

    /**
     * Event to check if veinmine should continue. Set allowContinue to false to cancel.
     */
    class PreToolUseCheck(
            pos: BlockPos,
            state: BlockState,
            player: PlayerEntity,
            tool: ItemStack,
            var allowContinue: Boolean = true
    ) : VeinMinerEvent(pos, state, player, tool)

    /**
     * Event fired after successful veinmine.
     */
    class PostToolUse(
            pos: BlockPos,
            state: BlockState,
            player: PlayerEntity,
            tool: ItemStack,
            val minerDataPre: MinerData
    ) : VeinMinerEvent(pos, state, player, tool)
}