package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.server.*
import com.spicymemes.veinminer.util.*
import net.minecraft.network.*
import net.minecraftforge.fmllegacy.network.*

class MinerActivatePacket(val keyActive: Boolean) : SpicyPacket {

    companion object Handler : SpicyPacketHandler<MinerActivatePacket> {

        override fun process(packet: MinerActivatePacket, ctx: NetworkEvent.Context) {
            val player = ctx.sender!!
            val status = MinerStatus.getForPlayer(player)

            if (status == null) {
                SpicyVeinMiner.logger.warn("Could not find player ${player.name} (${player.uuid}) in MinerStatus map after " +
                        "receiving MinerActivatePacket!")
                return
            }

            if (packet.keyActive && status == PlayerStatus.INACTIVE)
                MinerStatus.setForPlayer(player, PlayerStatus.ACTIVE)
            else if (!packet.keyActive && status == PlayerStatus.ACTIVE)
                MinerStatus.setForPlayer(player, PlayerStatus.INACTIVE)
        }

        override fun encode(packet: MinerActivatePacket, buf: FriendlyByteBuf) {
            buf.writeBoolean(packet.keyActive)
        }

        override fun decode(buf: FriendlyByteBuf) = MinerActivatePacket(buf.readBoolean())
    }
}
