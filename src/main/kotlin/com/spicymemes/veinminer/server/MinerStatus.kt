package com.spicymemes.veinminer.server

import com.spicymemes.veinminer.util.*
import net.minecraft.world.entity.player.*
import java.util.*
import java.util.concurrent.*

object MinerStatus {

    private val players = ConcurrentHashMap<UUID, PlayerStatus>()

    fun removePlayer(player: Player) {
        players.remove(player.uuid)
    }

    fun getForPlayer(player: Player) = players[player.uuid]

    fun setForPlayer(player: Player, status: PlayerStatus) {
        players[player.uuid] = status
    }
}