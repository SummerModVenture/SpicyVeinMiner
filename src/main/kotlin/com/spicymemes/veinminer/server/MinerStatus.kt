package com.spicymemes.veinminer.server

import com.spicymemes.veinminer.util.*
import net.minecraft.entity.player.*
import java.util.*
import java.util.concurrent.*

object MinerStatus {

    private val players = ConcurrentHashMap<UUID, PlayerStatus>()

    fun removePlayer(player: PlayerEntity) {
        players.remove(player.uuid)
    }

    fun getForPlayer(player: PlayerEntity) = players[player.uuid]

    fun setForPlayer(player: PlayerEntity, status: PlayerStatus) {
        players[player.uuid] = status
    }
}