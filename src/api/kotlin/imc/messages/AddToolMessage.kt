package com.spicymemes.veinminer.api.imc.messages

import com.spicymemes.veinminer.api.*
import net.minecraft.resources.*

@JvmRecord
data class AddToolMessage(val toolType: ToolType, val tool: ResourceLocation) {

    companion object : SpicyIMCMessageType<AddToolMessage>("addTool")
}
