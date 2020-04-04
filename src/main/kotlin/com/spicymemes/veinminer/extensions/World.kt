package com.spicymemes.veinminer.extensions

import net.minecraft.world.*
import net.minecraft.world.server.*

fun IWorld.toServerWorld() = this as ServerWorld
