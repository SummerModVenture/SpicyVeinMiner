package net.masterzach32.spicyminer.server

import com.spicymemes.core.util.getBlock
import com.spicymemes.core.util.serverOnly
import net.masterzach32.spicyminer.MOD_ID
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.network.PingClientPacket
import net.masterzach32.spicyminer.util.PlayerStatus
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemPickaxe
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent

@Suppress("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
object EventHandler {

    @JvmStatic
    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        serverOnly(event.world) {
            val tool = event.player.getHeldItem(event.player.activeHand)
            if (VeinMinerHelper.isValidTool(tool)) {
                var activate = false

                val status = PlayerManager.getPlayerStatus(event.player.uniqueID)
                if (status == PlayerStatus.ACTIVE)
                    activate = true
                else if (status == PlayerStatus.SNEAK_ACTIVE && event.player.isSneaking)
                    activate = true
                else if (status == PlayerStatus.SNEAK_INACTIVE && !event.player.isSneaking)
                    activate = true

                if (activate) {
                    val biw = event.world.getBlock(event.pos)
                    VeinMinerHelper.harvestBlocks(biw, event.player, tool, VeinMinerHelper.getAlikeBlocks(biw))
                }
            }
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerConnected(event: PlayerEvent.PlayerLoggedInEvent) {
        SpicyVeinMiner.network.sendTo(PingClientPacket(), event.player as EntityPlayerMP)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerDisconnected(event: PlayerEvent.PlayerLoggedOutEvent) {
        PlayerManager.removePlayer(event.player.uniqueID)
    }
}