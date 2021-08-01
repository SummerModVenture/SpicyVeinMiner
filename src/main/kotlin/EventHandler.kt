package com.spicymemes.veinminer

import com.spicymemes.api.*
import com.spicymemes.api.extensions.*
import com.spicymemes.veinminer.api.*
import com.spicymemes.veinminer.commands.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.network.*
import com.spicymemes.veinminer.network.packets.*
import net.minecraftforge.event.*
import net.minecraftforge.event.entity.player.*
import net.minecraftforge.event.world.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.common.*
import net.minecraftforge.fmllegacy.network.*
import java.time.*

@Suppress("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
object EventHandler {

    @JvmStatic
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        ModCommands.registerAll(event.dispatcher)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerConnected(event: PlayerEvent.PlayerLoggedInEvent) {
        Network.mainChannel.send(
                PacketDistributor.PLAYER.with { event.player.asServerPlayer() },
                PingClientPacket(Instant.now().toEpochMilli())
        )
        event.player.level.asServerWorld().minerData.initializePlayer(event.player)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerDisconnected(event: PlayerEvent.PlayerLoggedOutEvent) {
        MinerStatus.removePlayer(event.player)
    }

    @JvmStatic
    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        serverOnly(event.world) {
            if (event.player.isCreative)
                return

            var activate = false

            val status = MinerStatus.getForPlayer(event.player)
            if (status == PlayerStatus.ACTIVE)
                activate = true
            else if (status == PlayerStatus.SNEAK_ACTIVE && event.player.isCrouching)
                activate = true
            else if (status == PlayerStatus.SNEAK_INACTIVE && !event.player.isCrouching)
                activate = true

            if (activate)
                VeinMinerHelper.attemptExcavate(event)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        ServerConfig.blocksPerTick.get().let { blocksPerTick ->
            if (blocksPerTick == 0)
                VeinMinerHelper.processAllBlocks()
            else
                VeinMinerHelper.processLimitedBlocks(blocksPerTick)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onPostToolUse(event: VeinMinerEvent.PostToolUse) {
        serverOnly(event.player.commandSenderWorld) {
            val minerDataPost = event.player.asServerPlayer().minerData
            if (minerDataPost.level > event.minerDataPre.level) {
                //TODO: Find fix
//            event.player.sendMessage(TextComponentTranslation("SpicyVeinMiner: LEVEL UP ${event.minerDataPre.level} >>>" +
//                    " ${minerDataPost.level}. You can now mine up to ${minerDataPost.blockLimit} additional blocks at once."))
            }
        }
    }
}
