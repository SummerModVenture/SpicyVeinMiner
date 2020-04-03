package net.masterzach32.spicyminer.server

import net.masterzach32.spicyminer.util.PlayerStatus
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object MinerStatus {

    private val players = ConcurrentHashMap<UUID, PlayerStatus>()

    fun removePlayer(name: UUID) {
        players.remove(name)
    }

    fun getPlayerStatus(name: UUID) = players[name]

    fun setPlayerStatus(name: UUID, status: PlayerStatus) {
        players[name] = status
    }
}