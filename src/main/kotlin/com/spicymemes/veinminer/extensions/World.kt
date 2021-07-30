package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.server.*
import net.minecraft.server.level.*
import net.minecraft.world.level.*

fun LevelReader.asServerWorld(): ServerLevel = this as ServerLevel

val ServerLevel.minerData: MinerDataStorage
    get() =  server.overworld().dataStorage.computeIfAbsent(
        { MinerDataStorage.from(it) },
        { MinerDataStorage() },
        MinerDataStorage.ID
    )

