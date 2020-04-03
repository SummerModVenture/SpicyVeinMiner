package net.masterzach32.spicyminer.server.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import java.util.*

class PlayerManager(name: String = ID) : WorldSavedData(ID) {

    val data = mutableMapOf<UUID, Int>()

    fun initializePlayer(player: EntityPlayer) {
        if (!data.containsKey(player.uniqueID))
            data[player.uniqueID] = 0
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        nbt.setInteger("count", data.size)
        data.toList().forEachIndexed { i, (name, blocksMined) ->
            nbt.apply {
                setUniqueId("name$i", name)
                setInteger("blocks$i", blocksMined)
            }
        }
        return nbt
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        for (i in 0 until nbt.getInteger("count"))
            nbt.apply { data[getUniqueId("name$i")!!] = getInteger("blocks$i") }
        println(data)
    }

    companion object {
        val ID = "SpicyMinerPlayerData"

        fun getForWorld(world: World): PlayerManager {
            var data = world.perWorldStorage.getOrLoadData(PlayerManager::class.java, ID)
            if (data == null) {
                data = PlayerManager()
                world.perWorldStorage.setData(ID, data)
            }
            return data as PlayerManager
        }
    }
}