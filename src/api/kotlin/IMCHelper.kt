package com.spicymemes.veinminer.api

import com.spicymemes.veinminer.api.imc.messages.*
import net.minecraft.resources.*
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