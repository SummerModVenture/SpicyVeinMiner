package com.spicymemes.veinminer

import com.spicymemes.api.*
import com.spicymemes.veinminer.api.*
import net.minecraft.nbt.*
import net.minecraft.world.entity.player.*
import net.minecraft.world.level.saveddata.*
import java.util.*
import java.util.concurrent.*

class MinerDataStorage(private val data: MutableMap<UUID, MinerData> = ConcurrentHashMap()) : SavedData() {

    fun initializePlayer(player: Player) {
        if (!data.containsKey(player.uuid)) {
            data[player.uuid] = MinerData(player.uuid)
            isDirty = true
        }
    }

    fun getForPlayer(player: Player) = data[player.uuid]
            ?: error("Could not find player ${player.uuid} in map.")

    fun addBlocksMined(player: Player, numBlocks: Int) {
        data[player.uuid] = data[player.uuid]
            ?.let { it.copy(blocksMined = it.blocksMined + numBlocks) }
            ?.also { isDirty = true }
            ?: error("Could not find player ${player.uuid} in map.")
    }

    override fun save(nbt: CompoundTag): CompoundTag {
        nbt["count"] = data.size
        data.toList().forEachIndexed { i, (name, data) ->
            nbt.apply {
                putUUID("name$i", name)
                putInt("blocks$i", data.blocksMined)
            }
        }
        return nbt
    }

    companion object {
        val ID = "${MOD_ID}_SpicyMinerPlayerData"

        fun from(tag: CompoundTag): MinerDataStorage {
            val data = ConcurrentHashMap<UUID, MinerData>()
            val count: Int by tag
            for (i in 0 until count)
                tag.apply {
                    getUUID("name$i").also {
                        data[it] = MinerData(it, getInt("blocks$i"))
                    }
                }

            return MinerDataStorage(data)
        }
    }
}
