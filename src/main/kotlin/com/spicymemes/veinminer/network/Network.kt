package com.spicymemes.veinminer.network

import com.spicymemes.core.network.*
import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.network.packets.*
import net.minecraftforge.fml.network.simple.*

object Network {

    val mainChannel: SimpleChannel = newSimpleChannel("1", MOD_ID)

    fun registerPackets() {
        var id = 0
        mainChannel.registerPacket(id++, PingClientPacket)
        mainChannel.registerPacket(id++, ClientPresentPacket)
        mainChannel.registerPacket(id++, MinerActivatePacket)
        mainChannel.registerPacket(id++, ChangeModePacket)
        logger.info("Registered $id packets on the main network.")
    }
}
