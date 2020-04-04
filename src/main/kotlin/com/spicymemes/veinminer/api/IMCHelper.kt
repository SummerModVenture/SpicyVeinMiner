package com.spicymemes.veinminer.api

import com.spicymemes.veinminer.api.imcmessages.*
import com.spicymemes.veinminer.config.*
import net.minecraft.util.*
import net.minecraftforge.fml.*

object IMCHelper {

    private const val modid = "spicyminer"

    @JvmStatic
    fun addTool(type: String, name: ResourceLocation): Boolean {
        return InterModComms.sendTo(modid, AddToolMessage.method) { AddToolMessage(ToolType(type), name) }
    }

    @JvmStatic
    fun addBlockWhitelist(name: ResourceLocation): Boolean {
        return InterModComms.sendTo(modid, AddBlockMessage.method) { AddBlockMessage(name) }
    }
}