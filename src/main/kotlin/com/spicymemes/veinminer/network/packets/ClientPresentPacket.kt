package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.server.*
import com.spicymemes.veinminer.util.*
import net.minecraft.network.*
import net.minecraftforge.fml.network.*
import java.time.*

/**
 * Packet to alert the server that the connecting player has this mod installed.
 */
class ClientPresentPacket(val timestamp: Long, val mode: PreferredMode) : SpicyPacket {

    companion object Handler : SpicyPacketHandler<ClientPresentPacket> {

        override fun process(packet: ClientPresentPacket, ctx: NetworkEvent.Context) {
            val player = ctx.sender!!
            logger.info("Player ${player.uniqueID} has client mod installed. Ping took " +
                    "${Instant.now().toEpochMilli() - packet.timestamp} ms. Set preferred mode: ${packet.mode}")

            when (packet.mode) {
                PreferredMode.DISABLED,
                PreferredMode.PRESSED,
                PreferredMode.RELEASED ->
                    MinerStatus.setForPlayer(player, PlayerStatus.INACTIVE)
                PreferredMode.SNEAK_ACTIVE ->
                    MinerStatus.setForPlayer(player, PlayerStatus.SNEAK_ACTIVE)
                PreferredMode.SNEAK_INACTIVE ->
                    MinerStatus.setForPlayer(player, PlayerStatus.SNEAK_INACTIVE)
            }
        }

        override fun encode(packet: ClientPresentPacket, buf: PacketBuffer) {
            buf.writeLong(packet.timestamp)
            buf.writeShort(packet.mode.ordinal)
        }

        override fun decode(buf: PacketBuffer) = ClientPresentPacket(
                buf.readLong(),
                PreferredMode.values()[buf.readShort().toInt()]
        )
    }
}
