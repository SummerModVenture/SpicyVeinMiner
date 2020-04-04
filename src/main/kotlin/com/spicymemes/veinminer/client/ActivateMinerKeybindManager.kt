package com.spicymemes.veinminer.client

import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.network.*
import com.spicymemes.veinminer.network.packets.*
import com.spicymemes.veinminer.util.*
import net.minecraft.client.settings.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.client.registry.*

@OnlyIn(Dist.CLIENT)
object ActivateMinerKeybindManager {

    private var statusEnabled = false
    private val activateKey = KeyBinding("spicyveinminer.key.enable", 96, "spicyveinminer.key.category")

    init {
        ClientRegistry.registerKeyBinding(activateKey)
    }

    @SubscribeEvent
    fun onKeyPressed(event: InputEvent) {
        if (preferredMode != PreferredMode.PRESSED && preferredMode != PreferredMode.RELEASED)
            return

        var sendPacket = false
        val pressed = activateKey.isKeyDown

        if (preferredMode == PreferredMode.PRESSED) {
            if (pressed && !statusEnabled) {
                sendPacket = true
                statusEnabled = true
            } else if (!pressed && statusEnabled) {
                sendPacket = true
                statusEnabled = false
            }
        } else {
            if (!pressed && !statusEnabled) {
                sendPacket = true
                statusEnabled = true
            } else if (pressed && statusEnabled) {
                sendPacket = true
                statusEnabled = false
            }
        }

        if (sendPacket)
            Network.mainChannel.sendToServer(MinerActivatePacket(statusEnabled))
    }
}