package com.spicymemes.veinminer.api.imcmessages

import com.spicymemes.veinminer.api.*
import net.minecraft.resources.*

@JvmRecord
data class AddBlockMessage(val name: ResourceLocation) {

    companion object : SpicyIMCMessageType<AddBlockMessage>("addWhitelistBlock")
}
