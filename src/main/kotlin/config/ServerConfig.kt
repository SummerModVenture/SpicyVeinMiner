package com.spicymemes.veinminer.config

import com.spicymemes.api.config.*
import net.minecraft.resources.*
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
    val defaultAllowedTools = WhitelistTools.HAMMER_ARRAY + WhitelistTools.DRILL_ARRAY +
            WhitelistTools.PICKAXE_ARRAY + WhitelistTools.SHOVEL_ARRAY +
            WhitelistTools.AXE_ARRAY + WhitelistTools.MULTITOOL_ARRAY
    val allowedToolsSet: Set<ResourceLocation>
        get() = allowedTools.get().mapNotNull { ResourceLocation.tryParse(it) }.toSet()

    val allowedBlocks: ForgeConfigSpec.ConfigValue<List<String>>
    val defaultAllowedBlocks = WhitelistBlocks.VANILLA_BLOCKS
    val allowedBlocksSet: Set<ResourceLocation>
        get() = allowedBlocks.get().mapNotNull { ResourceLocation.tryParse(it) }.toSet()

    init {
        configSpec = config("server") {
            section("settings") {
                limit = comment("How many blocks total can be excavated.")
                    .defineInRange("limit", 100, 2, 200)
                blocksPerTick = comment("How many blocks can be destroyed per tick, lower amounts may reduce lag on " +
                        "large servers. Set to 0 for no limit.")
                    .defineInRange("blocksPerTick", 0, 0, 1000000)
                range = comment("How far from the initial block can blocks be excavated.")
                    .defineInRange("range", 20, 2, 50)
                exhaustion = comment("How much exhaustion the player takes for each block broken.")
                    .defineInRange("exhaustion", 0.01, 0.0, 1.0)
                exhaustionMultiplier = comment("How much exhaustion is multiplied when player doesn't use a tool.")
                    .defineInRange("exhaustionMultiplier", 3.0, 1.0, 5.0)
                ignoreTools = comment("Ignore whether the held tool is valid.")
                    .define("ignoreTools", false)
            }

            section("whitelist") {
                allowedTools = comment("Tools that can be used to veinmine.")
                    .defineList("allowedTools", defaultAllowedTools.toList()) { newValue ->
                        newValue is String && ResourceLocation.tryParse(newValue) != null
                    }

                allowedBlocks = comment("Blocks that can be veinmined.")
                    .defineList("allowedBlocks", defaultAllowedBlocks.toList()) { newValue ->
                        newValue is String && ResourceLocation.tryParse(newValue) != null
                    }
            }
        }
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
