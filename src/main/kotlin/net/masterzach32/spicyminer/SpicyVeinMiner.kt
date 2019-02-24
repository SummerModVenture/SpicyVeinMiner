package net.masterzach32.spicyminer

import com.spicymemes.core.util.clientOnly
import net.masterzach32.spicyminer.client.ActivateMinerKeybindManager
import net.masterzach32.spicyminer.client.ChangeModeCommand
import net.masterzach32.spicyminer.network.ChangeModePacket
import net.masterzach32.spicyminer.network.ClientPresentPacket
import net.masterzach32.spicyminer.network.MinerActivatePacket
import net.masterzach32.spicyminer.network.PingClientPacket
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.*
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

/*
 * SpicyVeinMiner - Created on 5/22/2018
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 5/22/2018
 */
@Mod(modid = MOD_ID, name = MOD_NAME, version = MOD_VERSION, modLanguage = "kotlin",
        modLanguageAdapter = "com.spicymemes.core.KotlinAdapter")
object SpicyVeinMiner {

    val network: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog

        clientOnly {
            MinecraftForge.EVENT_BUS.register(ActivateMinerKeybindManager)
            ActivateMinerKeybindManager.init()

            ClientCommandHandler.instance.registerCommand(ChangeModeCommand())
        }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        network.registerMessage(PingClientPacket.Handler::class.java, PingClientPacket::class.java, 0, Side.CLIENT)
        network.registerMessage(ClientPresentPacket.Handler::class.java, ClientPresentPacket::class.java, 1, Side.SERVER)
        network.registerMessage(MinerActivatePacket.Handler::class.java, MinerActivatePacket::class.java, 2, Side.SERVER)
        network.registerMessage(ChangeModePacket.Handler::class.java, ChangeModePacket::class.java, 3, Side.SERVER)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {}
}
