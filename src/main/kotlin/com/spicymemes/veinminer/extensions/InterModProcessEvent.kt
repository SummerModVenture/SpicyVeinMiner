package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.api.*
import net.minecraftforge.fml.*
import net.minecraftforge.fml.event.lifecycle.*
import kotlin.streams.*

fun <MSG> InterModProcessEvent.getMessagesOf(
        type: SpicyIMCMessageType<MSG>
): List<Pair<InterModComms.IMCMessage, MSG>> = getIMCStream { it == type.method }
        .map { it to it.getMessageSupplier<MSG>().get() }
        .toList()