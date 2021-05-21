package com.spicymemes.veinminer

import com.spicymemes.veinminer.api.imcmessages.*
import com.spicymemes.veinminer.client.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.network.*
import net.minecraftforge.common.*
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.*
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.*
import net.minecraftforge.fml.event.lifecycle.*
import net.minecraftforge.fml.network.*
import org.apache.commons.lang3.tuple.Pair
import org.apache.logging.log4j.LogManager
import java.util.function.*

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
class SpicyVeinMiner {

    init {
        logger = LogManager.getLogger("SpicyMiner")
        container = ModLoadingContext.get().activeContainer
        Config.register()
        container.registerExtensionPoint(ExtensionPoint.DISPLAYTEST) {
            Pair.of<Supplier<String>, BiPredicate<String, Boolean>>(
                Supplier { FMLNetworkConstants.IGNORESERVERONLY },
                BiPredicate { _, _ -> true }
            )
        }
    }

    companion object {

        lateinit var container: ModContainer

        @SubscribeEvent
        @JvmStatic
        fun setupCommon(event: FMLCommonSetupEvent) {
            logger = LogManager.getLogger("SpicyMiner")
            Network.registerPackets()
        }

        @SubscribeEvent
        @JvmStatic
        fun setupClient(event: FMLClientSetupEvent) {
            MinerClient.init()
        }

        @SubscribeEvent
        @JvmStatic
        fun setupServer(event: FMLDedicatedServerSetupEvent) {
        }

        @SubscribeEvent
        @JvmStatic
        fun enqueueIMC(event: InterModEnqueueEvent) {}

        @SubscribeEvent
        @JvmStatic
        fun processIMC(event: InterModProcessEvent) {
            event.getMessagesOf(AddToolMessage)
                    .groupBy { (_, obj) -> obj.toolType }
                    .mapValues { (_, msgs) -> msgs.map { (msg, obj) -> obj.tool } }
                    .forEach { (toolType, tools) ->
                        logger.info("Adding ${tools.size} tools from IMC for tool type ${toolType.name}.")
                        ServerConfig.addTools(tools.toSet())
                    }

            event.getMessagesOf(AddBlockMessage)
                    .forEach { (msg, obj) ->
                        logger.info("${msg.modId} adding block from IMC to veinminer whitelist: ${obj.name}")
                        ServerConfig.addBlock(obj.name)
                    }
        }

        @SubscribeEvent
        @JvmStatic
        fun setupComplete(event: FMLLoadCompleteEvent) {}
    }
}
