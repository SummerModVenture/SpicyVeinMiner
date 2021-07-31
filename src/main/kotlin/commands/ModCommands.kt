package com.spicymemes.veinminer.commands

import com.mojang.brigadier.*
import net.minecraft.commands.*

object ModCommands {

    fun registerAll(dispatcher: CommandDispatcher<CommandSourceStack>) {
        ChangeModeCommand.register(dispatcher)
    }
}
