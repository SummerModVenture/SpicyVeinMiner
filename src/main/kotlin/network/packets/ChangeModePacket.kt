package com.spicymemes.veinminer.network.packets

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.util.*
import net.minecraft.network.*
import net.minecraftforge.fmllegacy.network.*

open class ChangeModePacket(val mode: PreferredMode) : SpicyPacket {

    companion object Handler : SpicyPacketHandler<ChangeModePacket> {

        override fun process(packet: ChangeModePacket, ctx: NetworkEvent.Context) {
            SpicyVeinMiner.logger.debug("Changing preferredMode to ${packet.mode}.")

            ClientConfig.preferredMode.set(packet.mode)
        }

        override fun encode(packet: ChangeModePacket, buf: FriendlyByteBuf) {
            buf.writeShort(packet.mode.ordinal)
        }

        override fun decode(buf: FriendlyByteBuf) = ChangeModePacket(PreferredMode.values()[buf.readShort().toInt()])
    }
}
