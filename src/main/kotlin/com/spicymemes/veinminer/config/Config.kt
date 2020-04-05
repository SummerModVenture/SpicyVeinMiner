package com.spicymemes.veinminer.config

import com.spicymemes.veinminer.*
import net.minecraftforge.common.*
import net.minecraftforge.fml.config.*
import net.minecraftforge.fml.loading.*


object Config {

    val configDir = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(MOD_ID), MOD_NAME)

    fun register() {
        registerConfig(ModConfig.Type.COMMON, ServerConfig.configSpec, "server")
        registerConfig(ModConfig.Type.CLIENT, ClientConfig.configSpec, "client")
    }

    private fun registerConfig(type: ModConfig.Type, spec: ForgeConfigSpec, fileName: String) {
        SpicyVeinMiner.container.addConfig(SpicyModConfig(type, spec, SpicyVeinMiner.container, fileName))
    }
}