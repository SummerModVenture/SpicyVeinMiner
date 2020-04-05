package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.network.Network
import net.minecraft.network.*
import net.minecraftforge.fml.network.*

/**
 * Packet to ping the client to see if this mod is installed.
 */
class PingClientPacket(val timestamp: Long) : SpicyPacket {

    object Handler : SpicyPacketHandler<PingClientPacket> {

        override fun process(packet: PingClientPacket, ctx: NetworkEvent.Context) {
            logger.info("Received ping packet from server, sending client preferred mode back.")
            Network.mainChannel.sendToServer(ClientPresentPacket(packet.timestamp, ClientConfig.preferredMode.get()))
        }

        override fun encode(packet: PingClientPacket, buf: PacketBuffer) {
            buf.writeLong(packet.timestamp)
        }

        override fun decode(buf: PacketBuffer) = PingClientPacket(buf.readLong())
    }
}