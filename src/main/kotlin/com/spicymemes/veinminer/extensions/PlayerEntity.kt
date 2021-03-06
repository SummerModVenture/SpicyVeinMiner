package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.network.*
import com.spicymemes.veinminer.network.packets.*
import com.spicymemes.veinminer.server.*
import com.spicymemes.veinminer.util.*
import net.minecraft.entity.player.*
import net.minecraftforge.fml.network.*

val ServerPlayerEntity.minerData: MinerData
    get() = server.overworld().minerData.getForPlayer(this)

fun ServerPlayerEntity.addBlocksMined(num: Int) = server.overworld().minerData.addBlocksMined(this, num)

fun ServerPlayerEntity.setPreferredMode(mode: PreferredMode) {
    when (mode) {
        PreferredMode.SNEAK_ACTIVE -> MinerStatus.setForPlayer(this, PlayerStatus.SNEAK_ACTIVE)
        PreferredMode.SNEAK_INACTIVE -> MinerStatus.setForPlayer(this, PlayerStatus.SNEAK_INACTIVE)
        else -> MinerStatus.setForPlayer(this, PlayerStatus.INACTIVE)
    }

    Network.mainChannel.send(PacketDistributor.PLAYER.with { this }, ChangeModePacket(mode))
}
