package net.masterzach32.spicyminer.extensions

import net.masterzach32.spicyminer.server.player.*
import net.minecraft.entity.player.*

val EntityPlayer.minerData: PlayerData
    get() = PlayerData(uniqueID, PlayerManager.getForWorld(world).data[uniqueID]!!)

fun EntityPlayer.addBlocksMined(num: Int): PlayerData {
    PlayerManager.getForWorld(world).apply {
        data[uniqueID] = data.getValue(uniqueID) + num
        markDirty()
        return PlayerData(uniqueID, data.getValue(uniqueID))
    }
}
