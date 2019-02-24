package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.logger
import net.masterzach32.spicyminer.server.PlayerManager
import net.masterzach32.spicyminer.util.PlayerStatus
import net.masterzach32.spicyminer.util.PreferredMode
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

open class ChangeModePacket(var mode: PreferredMode) : IMessage {

    constructor(mode: Int) : this(PreferredMode.values()[mode])

    // default constructor for forge
    constructor() : this(PreferredMode.DISABLED)

    override fun fromBytes(buf: ByteBuf) {
         mode = PreferredMode.values()[buf.readShort().toInt()]
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeShort(mode.ordinal)
    }

    class Handler : GenericPacketHandler<ChangeModePacket>() {

        override fun processMessage(message: ChangeModePacket, ctx: MessageContext) {
            val player = ctx.serverHandler.player
            val playerName = player.uniqueID
            logger.info("Player $playerName requested to change mode to ${message.mode}.")

            when(message.mode) {
                PreferredMode.DISABLED,
                PreferredMode.PRESSED -> {
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.INACTIVE)
                    player.sendMessage(TextComponentTranslation("mod.spicyveinminer.preferredmode.auto"))
                }
                PreferredMode.SNEAK_ACTIVE -> {
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE)
                    player.sendMessage(TextComponentTranslation("mod.spicyveinminer.preferredmode.sneak"))
                }
                PreferredMode.SNEAK_INACTIVE -> {
                    PlayerManager.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE)
                    player.sendMessage(TextComponentTranslation("mod.spicyveinminer.preferredmode.nosneak"))
                }
            }
        }
    }
}