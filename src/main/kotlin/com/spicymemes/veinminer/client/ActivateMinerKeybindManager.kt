package com.spicymemes.veinminer.client

import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.network.*
import com.spicymemes.veinminer.network.packets.*
import com.spicymemes.veinminer.util.*
import net.minecraft.client.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.event.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fmlclient.registry.*
import org.lwjgl.glfw.*

@OnlyIn(Dist.CLIENT)
object ActivateMinerKeybindManager {

    private val inGame: Boolean
        get() = Minecraft.getInstance().connection != null
    private var statusEnabled = false
    private val activateKey = KeyMapping("spicyveinminer.key.enable", GLFW.GLFW_KEY_GRAVE_ACCENT, "spicyveinminer.key.category")

    init {
        ClientRegistry.registerKeyBinding(activateKey)
    }

    @SubscribeEvent
    fun onKeyPressed(event: InputEvent) {
        val preferredMode = ClientConfig.preferredMode.get()
        if (!inGame || (preferredMode != PreferredMode.PRESSED && preferredMode != PreferredMode.RELEASED))
            return

        var sendPacket = false
        val pressed = activateKey.isDown

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