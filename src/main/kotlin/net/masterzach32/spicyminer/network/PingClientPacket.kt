package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.client.preferredMode
import net.masterzach32.spicyminer.logger
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * Packet to ping the client to see if this mod is installed.
 */
class PingClientPacket : IMessage {

    override fun fromBytes(buf: ByteBuf) {}

    override fun toBytes(buf: ByteBuf) {}

    class Handler : GenericPacketHandler<PingClientPacket>() {

        override fun processMessage(message: PingClientPacket, ctx: MessageContext) {
            logger.info("Recieved Spicy VeinMiner server ping packet.")
            SpicyVeinMiner.network.sendToServer(ClientPresentPacket(preferredMode))
        }
    }
}