package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.config.preferredMode
import net.masterzach32.spicyminer.logger
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.time.Instant

/**
 * Packet to ping the client to see if this mod is installed.
 */
class PingClientPacket(var timestamp: Long) : IMessage {

    constructor() : this(0L)

    override fun fromBytes(buf: ByteBuf) {
        timestamp = buf.readLong()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeLong(timestamp)
    }

    class Handler : GenericPacketHandler<PingClientPacket>() {

        override fun processMessage(message: PingClientPacket, ctx: MessageContext) {
            logger.info("Received ping packet from server, sending client preferred mode back.")
            SpicyVeinMiner.network.sendToServer(ClientPresentPacket(message.timestamp, preferredMode))
        }
    }
}