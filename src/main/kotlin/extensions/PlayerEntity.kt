package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.api.*
import com.spicymemes.veinminer.network.*
import com.spicymemes.veinminer.network.packets.*
import net.minecraft.server.level.*
import net.minecraftforge.fmllegacy.network.*

val ServerPlayer.minerData: MinerData
    get() = server.overworld().minerData.getForPlayer(this)

fun ServerPlayer.addBlocksMined(num: Int) = server.overworld().minerData.addBlocksMined(this, num)

fun ServerPlayer.setPreferredMode(mode: PreferredMode) {
    when (mode) {
        PreferredMode.SNEAK_ACTIVE -> MinerStatus.setForPlayer(this, PlayerStatus.SNEAK_ACTIVE)
        PreferredMode.SNEAK_INACTIVE -> MinerStatus.setForPlayer(this, PlayerStatus.SNEAK_INACTIVE)
        else -> MinerStatus.setForPlayer(this, PlayerStatus.INACTIVE)
    }

    Network.mainChannel.send(PacketDistributor.PLAYER.with { this }, ChangeModePacket(mode))
}
