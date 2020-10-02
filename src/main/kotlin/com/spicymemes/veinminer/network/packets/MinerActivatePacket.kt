package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.logger
import com.spicymemes.veinminer.server.*
import com.spicymemes.veinminer.util.PlayerStatus
import net.minecraft.network.*
import net.minecraftforge.fml.network.*

class MinerActivatePacket(val keyActive: Boolean) : SpicyPacket {

    companion object Handler : SpicyPacketHandler<MinerActivatePacket> {

        override fun process(packet: MinerActivatePacket, ctx: NetworkEvent.Context) {
            val player = ctx.sender!!
            val status = MinerStatus.getForPlayer(player)

            if (status == null) {
                logger.warn("Could not find player ${player.name} (${player.uniqueID}) in MinerStatus map after " +
                        "receiving MinerActivatePacket!")
                return
            }

            if (packet.keyActive && status == PlayerStatus.INACTIVE)
                MinerStatus.setForPlayer(player, PlayerStatus.ACTIVE)
            else if (!packet.keyActive && status == PlayerStatus.ACTIVE)
                MinerStatus.setForPlayer(player, PlayerStatus.INACTIVE)
        }

        override fun encode(packet: MinerActivatePacket, buf: PacketBuffer) {
            buf.writeBoolean(packet.keyActive)
        }

        override fun decode(buf: PacketBuffer) = MinerActivatePacket(buf.readBoolean())
    }
}
