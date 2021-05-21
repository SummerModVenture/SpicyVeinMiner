package com.spicymemes.veinminer.server

import com.spicymemes.veinminer.*
import net.minecraft.entity.player.*
import net.minecraft.nbt.*
import net.minecraft.world.*
import net.minecraft.world.storage.*
import java.util.*
import java.util.concurrent.*

class MinerDataStorage(name: String = ID) : WorldSavedData(name) {

    private val data = ConcurrentHashMap<UUID, MinerData>()

    fun initializePlayer(player: PlayerEntity) {
        if (!data.containsKey(player.uuid)) {
            data[player.uuid] = MinerData(player.uuid)
            isDirty = true
        }
    }

    fun getForPlayer(player: PlayerEntity) = data[player.uuid]
            ?: error("Could not find player ${player.uuid} in map.")

    fun addBlocksMined(player: PlayerEntity, numBlocks: Int) {
        data[player.uuid] = data[player.uuid]
                ?.let { it.copy(blocksMined = it.blocksMined + numBlocks) }
                ?.also { isDirty = true }
                ?: error("Could not find player ${player.uuid} in map.")
    }

    override fun save(nbt: CompoundNBT): CompoundNBT {
        nbt.putInt("count", data.size)
        data.toList().forEachIndexed { i, (name, data) ->
            nbt.apply {
                putUUID("name$i", name)
                putInt("blocks$i", data.blocksMined)
            }
        }
        return nbt
    }

    override fun load(nbt: CompoundNBT) {
        for (i in 0 until nbt.getInt("count"))
            nbt.apply {
                getUUID("name$i").also {
                    data[it] = MinerData(it, getInt("blocks$i"))
                }
            }
        println(data)
    }

    companion object {
        val ID = "${MOD_ID}_SpicyMinerPlayerData"
    }
}
