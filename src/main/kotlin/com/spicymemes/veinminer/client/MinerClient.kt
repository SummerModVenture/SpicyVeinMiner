package com.spicymemes.veinminer.client

import com.spicymemes.veinminer.*
import com.spicymemes.veinminer.client.gui.*
import net.minecraft.client.*
import net.minecraft.client.gui.screen.*
import net.minecraftforge.common.*
import net.minecraftforge.fml.*
import java.util.function.*

object MinerClient {

    fun init() {
        MinecraftForge.EVENT_BUS.register(ActivateMinerKeybindManager)
        ModList.get().getModContainerById(MOD_ID).ifPresent {
            it.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY) {
                BiFunction { mc: Minecraft, parent: Screen -> GuiConfig(parent) }
            }
        }
    }
}