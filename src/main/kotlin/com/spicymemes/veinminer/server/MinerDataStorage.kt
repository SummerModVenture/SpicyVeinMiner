package com.spicymemes.veinminer.server

import com.spicymemes.veinminer.*
import net.minecraft.entity.player.*
import net.minecraft.nbt.*
import net.minecraft.world.*
import net.minecraft.world.storage.*
import java.util.*
import java.util.concurrent.*

class MinerDataStorage(name: String = ID) : WorldSavedData(ID) {

    private val data = ConcurrentHashMap<UUID, MinerData>()

    fun initializePlayer(player: PlayerEntity) {
        if (!data.containsKey(player.uniqueID))
            data[player.uniqueID] = MinerData(player.uniqueID)
        markDirty()
    }

    fun getForPlayer(player: PlayerEntity) = data[player.uniqueID]
            ?: throw IllegalStateException("Could not find player ${player.uniqueID} in map.")

    fun addBlocksMined(player: PlayerEntity, numBlocks: Int) {
        data[player.uniqueID] = data[player.uniqueID]
                ?.let { it.copy(blocksMined = it.blocksMined + numBlocks) }
                ?.also { markDirty() }
                ?: throw IllegalStateException("Could not find player ${player.uniqueID} in map.")
    }

    override fun write(nbt: CompoundNBT): CompoundNBT {
        nbt.putInt("count", data.size)
        data.toList().forEachIndexed { i, (name, data) ->
            nbt.apply {
                putUniqueId("name$i", name)
                putInt("blocks$i", data.blocksMined)
            }
        }
        return nbt
    }

    override fun read(nbt: CompoundNBT) {
        for (i in 0 until nbt.getInt("count"))
            nbt.apply {
                getUniqueId("name$i").also {
                    data[it] = MinerData(it, getInt("blocks$i"))
                }
            }
        println(data)
    }

    companion object {
        val ID = "${MOD_ID}_SpicyMinerPlayerData"

        fun get(world: World): MinerDataStorage {
            return world.server!!.getWorld(World.OVERWORLD)!!.savedData.getOrCreate({ MinerDataStorage() }, ID)
        }
    }
}
