package com.spicymemes.veinminer.api.imcmessages

import com.spicymemes.veinminer.api.*
import com.spicymemes.veinminer.config.*
import net.minecraft.util.*

data class AddToolMessage(val toolType: ToolType, val tool: ResourceLocation) {

    companion object : SpicyIMCMessageType<AddToolMessage>("addTool")
}
