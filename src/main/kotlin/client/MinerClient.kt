package com.spicymemes.veinminer.client

import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.client.gui.*
import net.minecraftforge.common.*
import net.minecraftforge.fmlclient.*

object MinerClient {

    fun init() {
        MinecraftForge.EVENT_BUS.register(ActivateMinerKeybindManager)
        SpicyVeinMiner.container.registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory::class.java) {
            ConfigGuiHandler.ConfigGuiFactory { _, parent -> GuiConfig(parent) }
        }
    }
}