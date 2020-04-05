package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.server.*
import com.spicymemes.veinminer.util.*
import net.minecraft.network.*
import net.minecraftforge.fml.network.NetworkEvent

open class ChangeModePacket(val mode: PreferredMode) : SpicyPacket {

    object Handler : SpicyPacketHandler<ChangeModePacket> {

        override fun process(packet: ChangeModePacket, ctx: NetworkEvent.Context) {
            logger.debug("Changing preferredMode to ${packet.mode}.")

            ClientConfig.preferredMode.set(packet.mode)
        }

        override fun encode(packet: ChangeModePacket, buf: PacketBuffer) {
            buf.writeShort(packet.mode.ordinal)
        }

        override fun decode(buf: PacketBuffer) = ChangeModePacket(PreferredMode.values()[buf.readShort().toInt()])
    }
}
