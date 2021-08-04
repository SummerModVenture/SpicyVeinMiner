package com.spicymemes.veinminer.config

import com.spicymemes.api.config.*
import com.spicymemes.veinminer.*
import net.minecraftforge.common.*

object ClientConfig {

    val configSpec: ForgeConfigSpec

    // change back to val once contract is fixed
    val preferredMode: ForgeConfigSpec.EnumValue<PreferredMode>

    init {
        configSpec = config("client") {
            preferredMode = comment("Set your preferred activation mode.")
                    .defineEnum("preferredMode", PreferredMode.PRESSED)
        }
    }
}
