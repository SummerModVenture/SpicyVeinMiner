@file:JvmName("PlayerConfig")
@file:Config(modid = MOD_ID, name = "SpicyVeinMiner", category = "activator")

package net.masterzach32.spicyminer.client

import net.masterzach32.spicyminer.MOD_ID
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraftforge.common.config.Config

@Config.Comment(
        "Set the preferred activator mode for the veinminer.",
        "Available Modes: DISABLED, PRESSED, SNEAK_ACTIVE, SNEAK_INACTIVE"
)
@JvmField
var prefMode: String = PreferredMode.PRESSED.name

var preferredMode: PreferredMode
    get() = PreferredMode.valueOf(prefMode)
    set(value) { prefMode = value.name }