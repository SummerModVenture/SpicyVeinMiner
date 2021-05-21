package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.server.*
import net.minecraft.world.*
import net.minecraft.world.server.*

@Deprecated("Use asServerWorld() extension", ReplaceWith("asServerWorld()"))
fun IWorld.toServerWorld() = asServerWorld()

fun IWorld.asServerWorld(): ServerWorld = this as ServerWorld

val ServerWorld.minerData: MinerDataStorage
    get() =  server.overworld().dataStorage.computeIfAbsent({ MinerDataStorage() }, MinerDataStorage.ID)

