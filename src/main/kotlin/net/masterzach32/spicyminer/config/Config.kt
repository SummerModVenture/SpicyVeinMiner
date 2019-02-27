@file:JvmName("Config")
@file:Config(modid = MOD_ID, name = "SpicyVeinMiner", category = "general")

package net.masterzach32.spicyminer.config

import net.masterzach32.spicyminer.MOD_ID
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraftforge.common.config.Config

@Config.Comment(
        "Set the preferred activator mode for the veinminer."
)
@JvmField
var preferredMode: PreferredMode = PreferredMode.PRESSED

@Config.Comment(
        "How many blocks can be excavated at once."
)
@Config.RangeInt(min = 2, max = 200)
@JvmField
var limit: Int = 100

@Config.Comment(
        "How far from the initial block can blocks be excavated."
)
@Config.RangeInt(min = 2, max = 50)
@JvmField
var range: Int = 20

@Config.Comment(
        "How much exhaustion the player takes for each block broken."
)
@JvmField
var exhaustion: Double = 0.005

@Config.Comment(
        "Ignore whether the held tool is valid."
)
@JvmField
var ignoreTools: Boolean = false

@Config.Comment(
        "Only vanilla minecraft tools can be used to excavate."
)
@JvmField
var vanillaOnly: Boolean = false

@Config.Comment(
        "All tools that can be used to veinmine. Most tools should be auto-populated here."
)
@JvmField
var tools: Array<String> = listOf(
        Items.DIAMOND_PICKAXE,
        Items.GOLDEN_PICKAXE,
        Items.IRON_PICKAXE,
        Items.STONE_PICKAXE,
        Items.WOODEN_PICKAXE
).map { it.registryName.toString() }.toTypedArray()

@Config.Comment(
        "List of blocks that cannot be veinmined."
)
@JvmField
var blockBlacklist: Array<String> = listOf(
        Blocks.STONE,
        Blocks.DIRT,
        Blocks.SAND,
        Blocks.GRAVEL
).map { it.registryName.toString() }.toTypedArray()