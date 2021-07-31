package com.spicymemes.veinminer.client

import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.client.gui.*
import net.minecraft.client.*
import net.minecraft.client.gui.screens.*
import net.minecraftforge.common.*
import net.minecraftforge.fmlclient.*
import java.util.function.*

object MinerClient {

    fun init() {
        MinecraftForge.EVENT_BUS.register(ActivateMinerKeybindManager)
        SpicyVeinMiner.container.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory::class.java) {
            ConfigGuiHandler.ConfigGuiFactory(BiFunction { mc: Minecraft, parent: Screen -> GuiConfig(parent) })
        }
    }
}