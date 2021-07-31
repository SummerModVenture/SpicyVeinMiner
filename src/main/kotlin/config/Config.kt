package com.spicymemes.veinminer.config

import com.spicymemes.veinminer.*
import net.minecraftforge.fml.*
import net.minecraftforge.fml.config.*
import net.minecraftforge.fml.loading.*
import java.io.*
import java.nio.file.*

object Config {

    val configPath: Path = FMLPaths.CONFIGDIR.get()
    val modConfigPath: Path = Paths.get(configPath.toAbsolutePath().toString(), MOD_ID)

    fun register() {
        try {
            Files.createDirectory(modConfigPath)
        } catch (e: IOException) {
            logger.error("Failed to create spicyminer config directory.", e)
        }

        ModLoadingContext.get().apply {
            registerConfig(ModConfig.Type.COMMON, ServerConfig.configSpec, "$MOD_ID/server.toml")
            registerConfig(ModConfig.Type.CLIENT, ClientConfig.configSpec, "$MOD_ID/client.toml")
        }
    }
}