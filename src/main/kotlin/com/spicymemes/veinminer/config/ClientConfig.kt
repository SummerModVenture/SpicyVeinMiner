package com.spicymemes.veinminer.config

import com.spicymemes.veinminer.util.*
import net.minecraftforge.common.*

object ClientConfig {

    val configSpec: ForgeConfigSpec

    val preferredMode: ForgeConfigSpec.EnumValue<PreferredMode>

    init {
        configSpec = config("client") {
            preferredMode = comment("Set your preferred activation mode.")
                    .defineEnum("preferredMode", PreferredMode.PRESSED)
        }
    }
}
