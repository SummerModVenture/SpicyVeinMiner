package com.spicymemes.veinminer.extensions

import com.spicymemes.veinminer.api.*
import net.minecraftforge.fml.*
import net.minecraftforge.fml.event.lifecycle.*
import java.util.function.*

@Suppress("UNCHECKED_CAST")
fun <M> InterModProcessEvent.getMessagesOf(
        type: SpicyIMCMessageType<M>
): List<Pair<InterModComms.IMCMessage, M>> = getIMCStream { it == type.method }
        .map { it to (it.messageSupplier as Supplier<M>).get() }
        .toList()