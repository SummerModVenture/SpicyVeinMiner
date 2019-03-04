package net.masterzach32.spicyminer.client

import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.network.MinerActivatePacket
import net.masterzach32.spicyminer.config.preferredMode
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

@SideOnly(Side.CLIENT)
object ActivateMinerKeybindManager {

    private var statusEnabled = false
    private val activateKey = KeyBinding("spicyveinminer.key.enable", Keyboard.KEY_GRAVE, "spicyveinminer.key.category")

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
            SpicyVeinMiner.network.sendToServer(MinerActivatePacket(statusEnabled))
    }
}