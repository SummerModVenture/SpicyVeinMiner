package net.masterzach32.spicyminer.client

import net.masterzach32.spicyminer.SpicyVeinMiner
import net.masterzach32.spicyminer.network.ChangeModePacket
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.server.MinecraftServer

class ChangeModeCommand : CommandBase() {

    override fun getName() = "sveinminerc"

    override fun getRequiredPermissionLevel() = 0

    override fun checkPermission(server: MinecraftServer?, sender: ICommandSender) = true

    override fun execute(server: MinecraftServer?, sender: ICommandSender, args: Array<String>) {
        val player = sender as EntityPlayerSP
        val playerName = player.uniqueID

        if (args.size != 1)
            throw WrongUsageException(getUsage(sender))

        val newMode: PreferredMode = when {
            args[0] == modes[0] && preferredMode != PreferredMode.DISABLED -> PreferredMode.DISABLED
            args[0] == modes[1] && preferredMode != PreferredMode.PRESSED -> PreferredMode.PRESSED
            args[0] == modes[2] && preferredMode != PreferredMode.SNEAK_ACTIVE ->  PreferredMode.SNEAK_ACTIVE
            args[0] == modes[3] && preferredMode != PreferredMode.SNEAK_INACTIVE -> PreferredMode.SNEAK_INACTIVE
            else -> throw WrongUsageException(getUsage(sender))
        }

        preferredMode = newMode
        SpicyVeinMiner.network.sendToServer(ChangeModePacket(newMode))
    }

    override fun getUsage(sender: ICommandSender) = "/sveinminerc disabled/pressed/sneak/no_sneak"


    companion object {
        private val modes = arrayOf("disabled", "pressed", "sneak", "no_sneak")
    }
}