package com.spicymemes.veinminer

import com.spicymemes.veinminer.api.imc.messages.*
import com.spicymemes.veinminer.client.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.network.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.*
import net.minecraftforge.fml.common.*
import net.minecraftforge.fml.event.lifecycle.*
import net.minecraftforge.fmllegacy.network.*
import org.apache.logging.log4j.*

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
class SpicyVeinMiner {

    init {
        logger = LogManager.getLogger("SpicyMiner")
        ModLoadingContext.get().apply {
            container = activeContainer
            Config.register(this)
        }
        container.registerExtensionPoint(IExtensionPoint.DisplayTest::class.java) {
            IExtensionPoint.DisplayTest(
                { FMLNetworkConstants.IGNORESERVERONLY },
                { _, _ -> true }
            )
        }
    }

    companion object {

        lateinit var container: ModContainer
        lateinit var logger: Logger

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
        fun setupServer(event: FMLDedicatedServerSetupEvent) {}

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
                        logger.info("${msg.modId()} adding block from IMC to veinminer whitelist: ${obj.name}")
                        ServerConfig.addBlock(obj.name)
                    }
        }

        @SubscribeEvent
        @JvmStatic
        fun setupComplete(event: FMLLoadCompleteEvent) {}
    }
}
