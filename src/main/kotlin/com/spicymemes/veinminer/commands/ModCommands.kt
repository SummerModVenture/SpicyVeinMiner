package com.spicymemes.veinminer.commands

import com.mojang.brigadier.*
import net.minecraft.command.*

object ModCommands {

    fun registerAll(dispatcher: CommandDispatcher<CommandSource>) {
        ChangeModeCommand.register(dispatcher)
    }
}
