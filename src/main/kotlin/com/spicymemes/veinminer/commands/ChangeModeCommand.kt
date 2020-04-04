package com.spicymemes.veinminer.commands

import com.mojang.brigadier.*
import com.mojang.brigadier.context.*
import com.spicymemes.veinminer.config.*
import com.spicymemes.veinminer.extensions.*
import com.spicymemes.veinminer.util.*
import net.minecraft.command.*
import net.minecraft.entity.player.*
import net.minecraft.util.text.*

object ChangeModeCommand {

    fun register(dispatcher: CommandDispatcher<CommandSource>) {
        val literalArgumentBuilder = Commands.literal("sveinminerc").requires { it.hasPermissionLevel(1) }

        for (mode in PreferredMode.values()) {
            literalArgumentBuilder.then(Commands.literal(mode.codeName).executes {
                setPreferredMode(it, it.source.asPlayer(), mode)
            })
        }

        dispatcher.register(literalArgumentBuilder)
    }

    private fun sendFeedback(player: ServerPlayerEntity, newMode: PreferredMode) {
        player.sendMessage(TranslationTextComponent("commands.spicyminer.sveinminerc.success.${newMode.codeName}"))
    }

    private fun setPreferredMode(source: CommandContext<CommandSource>, player: ServerPlayerEntity, newMode: PreferredMode): Int {
        if (preferredMode != newMode) {
            player.setPreferredMode(newMode)
            sendFeedback(player, newMode)
            return 1
        } else
            return 0
    }
}