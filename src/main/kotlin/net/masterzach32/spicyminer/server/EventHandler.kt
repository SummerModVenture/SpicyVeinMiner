package net.masterzach32.spicyminer.server

import com.spicymemes.core.util.getBlock
import com.spicymemes.core.util.serverOnly
import net.masterzach32.spicyminer.MOD_ID
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.api.VeinMinerEvent
import net.masterzach32.spicyminer.config.blocksPerTick
import net.masterzach32.spicyminer.extensions.minerData
import net.masterzach32.spicyminer.logger
import net.masterzach32.spicyminer.network.PingClientPacket
import net.masterzach32.spicyminer.server.player.PlayerManager
import net.masterzach32.spicyminer.util.PlayerStatus
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.world.BlockEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.time.Instant

@Suppress("unused")
@Mod.EventBusSubscriber(modid = MOD_ID)
object EventHandler {

    @JvmStatic
    @SubscribeEvent
    fun onPlayerConnected(event: PlayerEvent.PlayerLoggedInEvent) {
        SpicyVeinMiner.network.sendTo(PingClientPacket(Instant.now().toEpochMilli()), event.player as EntityPlayerMP)
        PlayerManager.getForWorld(event.player.world).initializePlayer(event.player)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPlayerDisconnected(event: PlayerEvent.PlayerLoggedOutEvent) {
        MinerStatus.removePlayer(event.player.uniqueID)
    }

    @JvmStatic
    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        serverOnly(event.world) {
            val tool = event.player.heldItemMainhand
            var activate = false

            val status = MinerStatus.getPlayerStatus(event.player.uniqueID)
            if (status == PlayerStatus.ACTIVE)
                activate = true
            else if (status == PlayerStatus.SNEAK_ACTIVE && event.player.isSneaking)
                activate = true
            else if (status == PlayerStatus.SNEAK_INACTIVE && !event.player.isSneaking)
                activate = true

            if (activate)
                VeinMinerHelper.attemptExcavate(event.world.getBlock(event.pos), tool, event.player as EntityPlayerMP)
        }
    }

    @JvmStatic
    @SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
        if (blocksPerTick == 0)
            VeinMinerHelper.processAllBlocks()
        else
            VeinMinerHelper.processLimitedBlocks(blocksPerTick)
    }

    @JvmStatic
    @SubscribeEvent
    fun onPostToolUse(event: VeinMinerEvent.PostToolUse) {
        val minerDataPost = event.player.minerData
        logger.info("Blocks mined: ${minerDataPost.blocksMined}")
        if (minerDataPost.level > event.minerDataPre.level) {
            event.player.sendMessage(TextComponentTranslation("SpicyVeinMiner: LEVEL UP ${event.minerDataPre.level} >>>" +
                    " ${minerDataPost.level}. You can now mine up to ${minerDataPost.blockLimit} additional blocks at once."))
        }
    }
}