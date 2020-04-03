package net.masterzach32.spicyminer.server.player

import net.masterzach32.spicyminer.config.limit
import java.util.*
import kotlin.math.*
import kotlin.math.sqrt

data class PlayerData(
        val name: UUID,
        val blocksMined: Int
) {

    val level = 1 + (0.15*sqrt(blocksMined.toDouble())).toInt()
    val blockLimit = min(level*2, limit)
}