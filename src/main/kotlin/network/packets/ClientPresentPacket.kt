package com.spicymemes.veinminer.network.packets

import com.spicymemes.api.network.*
import com.spicymemes.veinminer.*
import net.minecraft.network.*
import net.minecraftforge.fmllegacy.network.*

/**
 * Packet to alert the server that the connecting player has this mod installed.
 */
class ClientPresentPacket(val timestamp: Long, val mode: PreferredMode) : SpicyPacket {

    companion object Handler : SpicyPacketHandler<ClientPresentPacket> {

        override fun process(packet: ClientPresentPacket, ctx: NetworkEvent.Context) {
            val player = ctx.sender!!
            SpicyVeinMiner.logger.info("Player ${player.name.contents} has client mod installed. Set preferred mode: ${packet.mode}")

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

        override fun encode(packet: ClientPresentPacket, buf: FriendlyByteBuf) {
            buf.writeLong(packet.timestamp)
            buf.writeShort(packet.mode.ordinal)
        }

        override fun decode(buf: FriendlyByteBuf) = ClientPresentPacket(
                buf.readLong(),
                PreferredMode.values()[buf.readShort().toInt()]
        )
    }
}
