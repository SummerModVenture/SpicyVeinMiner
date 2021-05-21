package com.spicymemes.veinminer.config

import net.minecraftforge.common.*
import kotlin.contracts.*

inline fun config(name: String, config: ForgeConfigSpec.Builder.() -> Unit): ForgeConfigSpec {
    contract { callsInPlace(config, InvocationKind.EXACTLY_ONCE) }
    return ForgeConfigSpec.Builder()
            .apply { section(name, config) }
            .build()
}

inline fun ForgeConfigSpec.Builder.section(name: String, config: ForgeConfigSpec.Builder.() -> Unit) {
    contract { callsInPlace(config, InvocationKind.EXACTLY_ONCE) }
    push(name)
    config()
    pop()
}
