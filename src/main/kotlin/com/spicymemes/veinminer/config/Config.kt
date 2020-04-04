@file:JvmName("ConfigOptions")
//@file:Config(modid = MOD_ID, name = MOD_NAME, category = "general")

package com.spicymemes.veinminer.config

import com.spicymemes.veinminer.util.*
import net.minecraft.block.*
import net.minecraft.util.*

//@Config.Comment("Set the preferred activator mode for the veinminer.")
@JvmField
var preferredMode: PreferredMode = PreferredMode.PRESSED

//@Config.Comment("How many blocks can be excavated at once.")
//@Config.RangeInt(min = 2, max = 200)
@JvmField
var limit: Int = 100

//@Config.Comment("How far from the initial block can blocks be excavated.")
//@Config.RangeInt(min = 2, max = 50)
@JvmField
var range: Int = 20

//@Config.Comment("How much exhaustion the player takes for each block broken.")
@JvmField
var exhaustion: Double = 0.005

//@Config.Comment("Ignore whether the held tool is valid.")
@JvmField
var ignoreTools: Boolean = false

//@Config.Comment("Only vanilla minecraft tools can be used to excavate.")
@JvmField
var vanillaOnly: Boolean = false

//@Config.Comment("How many blocks can be destroyed per tick, lower amounts may reduce lag on large servers. Set to 0 for no limit.")
//@Config.RangeInt(min = 0)
@JvmField
var blocksPerTick: Int = 0

object Config {

    private val tools = mutableMapOf<ToolType, List<ResourceLocation>>()
    val registeredTools: Map<ToolType, List<ResourceLocation>>
        get() = tools.toMap()

    private val blocks = mutableSetOf<ResourceLocation>()
    val registeredBlocks: Set<ResourceLocation>
        get() = blocks.toSet()

    fun hasToolType(toolType: ToolType) = tools.containsKey(toolType)

    fun addToolType(toolType: ToolType) {
        tools[toolType] = emptyList()
    }

    fun addTool(toolType: ToolType, tool: ResourceLocation) {
        if (hasToolType(toolType))
            tools[toolType] = tools[toolType]!!.toMutableList().apply { add(tool) }
        else {
            addToolType(toolType)
            tools[toolType] = tools[toolType]!!.toMutableList().apply { add(tool) }
        }
    }

    fun addTools(toolType: ToolType, list: List<ResourceLocation>) {
        if (hasToolType(toolType))
            tools[toolType] = tools[toolType]!!.toMutableList().apply { addAll(list) }
        else {
            addToolType(toolType)
            tools[toolType] = tools[toolType]!!.toMutableList().apply { addAll(list) }
        }
    }

    fun addBlock(name: ResourceLocation) {
        blocks.add(name)
    }
}