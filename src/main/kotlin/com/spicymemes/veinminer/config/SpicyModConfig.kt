package com.spicymemes.veinminer.config

import com.electronwill.nightconfig.core.file.*
import com.spicymemes.veinminer.*
import net.minecraftforge.common.*
import net.minecraftforge.fml.*
import net.minecraftforge.fml.config.*
import net.minecraftforge.fml.loading.*
import java.nio.file.*
import java.util.function.Function

class SpicyModConfig(
        type: Type,
        spec: ForgeConfigSpec,
        container: ModContainer,
        fileName: String
) : ModConfig(type, spec, container, "$MOD_ID/$fileName.toml") {

    override fun getHandler() = SpicyModConfig

    companion object : ConfigFileTypeHandler() {

        override fun reader(configBasePath: Path): Function<ModConfig, CommentedFileConfig> {
            if (configBasePath.endsWith("serverconfig"))
                return super.reader(FMLPaths.CONFIGDIR.get())
            return super.reader(configBasePath)
        }
    }
}
