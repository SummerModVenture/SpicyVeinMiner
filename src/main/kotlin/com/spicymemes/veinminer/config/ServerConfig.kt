package com.spicymemes.veinminer.config

import net.minecraft.util.*
import net.minecraftforge.common.*

object ServerConfig {

    val configSpec: ForgeConfigSpec

    val limit: ForgeConfigSpec.IntValue
    val blocksPerTick: ForgeConfigSpec.IntValue
    val range: ForgeConfigSpec.IntValue
    val exhaustion: ForgeConfigSpec.DoubleValue
    val exhaustionMultiplier: ForgeConfigSpec.DoubleValue
    val ignoreTools: ForgeConfigSpec.BooleanValue

    val allowedTools: ForgeConfigSpec.ConfigValue<List<String>>
    val allowedToolsSet: Set<ResourceLocation>
        get() = allowedTools.get().mapNotNull { ResourceLocation.tryCreate(it) }.toSet()

    val allowedBlocks: ForgeConfigSpec.ConfigValue<List<String>>
    val allowedBlocksSet: Set<ResourceLocation>
        get() = allowedBlocks.get().mapNotNull { ResourceLocation.tryCreate(it) }.toSet()

    init {
        val configBuilder = ForgeConfigSpec.Builder()

        configBuilder.push("server")

        configBuilder.push("settings")
        limit = configBuilder
                .comment("How many blocks total can be excavated.")
                .defineInRange("limit", 100, 2, 200)
        blocksPerTick = configBuilder
                .comment("How many blocks can be destroyed per tick, lower amounts may reduce lag on large servers. " +
                        "Set to 0 for no limit.")
                .defineInRange("blocksPerTick", 0, 0, 1000000)
        range = configBuilder
                .comment("How far from the initial block can blocks be excavated.")
                .defineInRange("range", 20, 2, 50)
        exhaustion = configBuilder
                .comment("How much exhaustion the player takes for each block broken.")
                .defineInRange("exhaustion", 0.01, 0.0, 1.0)
        exhaustionMultiplier = configBuilder
                .comment("How much exhaustion is multiplied when player doesn't use a tool.")
                .defineInRange("exhaustionMultiplier", 3.0, 1.0, 5.0)
        ignoreTools = configBuilder
                .comment("Ignore whether the held tool is valid.")
                .define("ignoreTools", false)
        configBuilder.pop()

        configBuilder.push("whitelist")
        allowedTools = configBuilder
                .comment("Tools that can be used to veinmine.")
                .defineList("allowedTools", listOf<String>()) { newValue ->
                    newValue is String && ResourceLocation.tryCreate(newValue) != null
                }

        allowedBlocks = configBuilder
                .comment("Blocks that can be veinmined.")
                .defineList("allowedBlocks", listOf<String>()) { newValue ->
                    newValue is String && ResourceLocation.tryCreate(newValue) != null
                }
        configBuilder.pop()

        configBuilder.pop()

        configSpec = configBuilder.build()
    }

    fun addTools(tools: Set<ResourceLocation>) {
        allowedTools.set(
                allowedToolsSet.toMutableSet()
                        .apply { addAll(tools) }
                        .map { it.toString() }
        )
    }

    fun addBlocks(blocks: Set<ResourceLocation>) {
        allowedBlocks.set(
                allowedBlocksSet.toMutableSet()
                        .apply { addAll(blocks) }
                        .map { it.toString() }
        )
    }

    fun addBlock(block: ResourceLocation) {
        addBlocks(setOf(block))
    }
}
