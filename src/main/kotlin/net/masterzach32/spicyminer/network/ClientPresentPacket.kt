package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.logger
import net.masterzach32.spicyminer.server.PlayerManager
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Packet to alert the server that the connecting player has this mod installed.
 */
class ClientPresentPacket: IMessage {

    override fun fromBytes(buf: ByteBuf) {}

    override fun toBytes(buf: ByteBuf) {}

    class Handler : GenericPacketHandler<ClientPresentPacket>() {

        override fun processMessage(message: ClientPresentPacket, ctx: MessageContext) {
            val player = ctx.serverHandler.player
            logger.info("Player ${player.uniqueID} has client mod installed.")
            PlayerManager.addPlayer(player.uniqueID)
        }
    }
}