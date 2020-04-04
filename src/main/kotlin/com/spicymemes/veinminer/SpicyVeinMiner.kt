package com.spicymemes.veinminer

import com.spicymemes.veinminer.api.imcmessages.*
import com.spicymemes.veinminer.client.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.network.*
import net.minecraftforge.common.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.*
import org.apache.logging.log4j.LogManager

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
class SpicyVeinMiner {

    companion object {

        @SubscribeEvent
        @JvmStatic
        fun setupCommon(event: FMLCommonSetupEvent) {
            logger = LogManager.getLogger("SpicyMiner")
            Network.registerPackets()
        }

        @SubscribeEvent
        @JvmStatic
        fun setupClient(event: FMLClientSetupEvent) {
            MinecraftForge.EVENT_BUS.register(ActivateMinerKeybindManager)
        }

        @SubscribeEvent
        @JvmStatic
        fun setupServer(event: FMLDedicatedServerSetupEvent) {

        }

        @SubscribeEvent
        @JvmStatic
        fun enqueueIMC(event: InterModEnqueueEvent) {
            WhitelistTools.addDefaultTools()
            WhitelistBlocks.addDefaultBlocks()
        }

        @SubscribeEvent
        @JvmStatic
        fun processIMC(event: InterModProcessEvent) {
            event.getMessagesOf(AddToolMessage)
                    .groupBy { (msg, obj) -> obj.toolType }
                    .mapValues { (toolType, msgs) -> msgs.map { (msg, obj) -> obj.tool } }
                    .forEach { (toolType, tools) ->
                        logger.info("Adding ${tools.size} tools from IMC to toolType=${toolType.name}.")
                        Config.addTools(toolType, tools)
                    }
            event.getMessagesOf(AddBlockMessage)
                    .forEach { (msg, obj) -> Config.addBlock(obj.name) }
        }
    }
}
