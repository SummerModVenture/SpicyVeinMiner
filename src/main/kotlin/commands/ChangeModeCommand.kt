package com.spicymemes.veinminer.commands

import com.mojang.brigadier.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.util.*
import net.minecraft.commands.*
import net.minecraft.network.chat.*
import net.minecraft.server.level.*

object ChangeModeCommand {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val literalArgumentBuilder = Commands.literal("veinminer")

        for (mode in PreferredMode.values()) {
            literalArgumentBuilder.then(Commands.literal(mode.codeName).executes {
                setPreferredMode(it.source.playerOrException, mode)
            })
        }

        dispatcher.register(literalArgumentBuilder)
    }

    private fun setPreferredMode(
        player: ServerPlayer,
        newMode: PreferredMode
    ): Int {
        player.setPreferredMode(newMode)
        player.sendMessage(
            TranslatableComponent("commands.spicyminer.command.success.${newMode.codeName}"),
            player.uuid
        )
        return 1
    }
}