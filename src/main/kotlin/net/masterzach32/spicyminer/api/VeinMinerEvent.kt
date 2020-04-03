package net.masterzach32.spicyminer.api

import net.masterzach32.spicyminer.server.player.PlayerData
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.eventhandler.Event

abstract class VeinMinerEvent(
        val pos: BlockPos,
        val state: IBlockState,
        val player: EntityPlayerMP,
        val tool: ItemStack
) : Event() {

    /**
     * Event to check if veinmine should continue. Set allowContinue to false to cancel.
     */
    class PreToolUseCheck(
            pos: BlockPos,
            state: IBlockState,
            player: EntityPlayerMP,
            tool: ItemStack,
            var allowContinue: Boolean = true
    ) : VeinMinerEvent(pos, state, player, tool)

    /**
     * Event fired after successful veinmine.
     */
    class PostToolUse(
            pos: BlockPos,
            state: IBlockState,
            player: EntityPlayerMP,
            tool: ItemStack,
            val minerDataPre: PlayerData
    ) : VeinMinerEvent(pos, state, player, tool)
}