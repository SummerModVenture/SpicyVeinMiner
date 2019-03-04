package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.logger
import net.masterzach32.spicyminer.server.PlayerManager
import net.masterzach32.spicyminer.util.PlayerStatus
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.time.Instant

/**
 * Packet to alert the server that the connecting player has this mod installed.
 */
class ClientPresentPacket(var timestamp: Long, var mode: PreferredMode) : IMessage {

    constructor(timestamp: Long, mode: Int) : this(timestamp, PreferredMode.values()[mode])

    // default constructor for forge
    constructor() : this(0L, PreferredMode.DISABLED)

    override fun fromBytes(buf: ByteBuf) {
        timestamp = buf.readLong()
        mode = PreferredMode.values()[buf.readShort().toInt()]
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeLong(timestamp)
        buf.writeShort(mode.ordinal)
    }

    class Handler : GenericPacketHandler<ClientPresentPacket>() {

        override fun processMessage(message: ClientPresentPacket, ctx: MessageContext) {
            val player = ctx.serverHandler.player
            val playerName = player.uniqueID
            logger.info("Player $playerName has client mod installed. Ping took " +
                    "${Instant.now().toEpochMilli() - message.timestamp} ms. Set preferred mode: ${message.mode}")
            PlayerManager.addPlayer(playerName)

            when (message.mode) {
                PreferredMode.DISABLED,
                PreferredMode.PRESSED,
                PreferredMode.RELEASED ->
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.INACTIVE)
                PreferredMode.SNEAK_ACTIVE ->
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE)
                PreferredMode.SNEAK_INACTIVE ->
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE)
            }
        }
    }
}