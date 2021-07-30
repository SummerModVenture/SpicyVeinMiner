package com.spicymemes.veinminer.server

import java.util.*
import kotlin.math.*

data class MinerData(
        val name: UUID,
        val blocksMined: Int = 0
) {

    val level = 1 + (0.15*sqrt(blocksMined.toDouble())).toInt()
    val blockLimit = 100// min(level*2, limit)
}