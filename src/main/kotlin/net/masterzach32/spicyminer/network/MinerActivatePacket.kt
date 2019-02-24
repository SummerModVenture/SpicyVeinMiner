package net.masterzach32.spicyminer.network

import com.spicymemes.core.network.GenericPacketHandler
import io.netty.buffer.ByteBuf
import net.masterzach32.spicyminer.logger
import net.masterzach32.spicyminer.server.PlayerManager
import net.masterzach32.spicyminer.util.PlayerStatus
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class MinerActivatePacket(var keyActive: Boolean) : IMessage {

    // default constructor for forge
    constructor() : this(false)

    override fun fromBytes(buf: ByteBuf) {
        keyActive = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeBoolean(keyActive)
    }

    class Handler : GenericPacketHandler<MinerActivatePacket>() {

        override fun processMessage(message: MinerActivatePacket, ctx: MessageContext) {
            val player = ctx.serverHandler.player
            val playerName = player.uniqueID
            val status = PlayerManager.getPlayerStatus(playerName)

            if (status == null) {
                logger.warn("Could not find player ${player.name} ($playerName) in PlayerManager map after receiving" +
                        " MinerActivatePacket!")
                return
            }

            if (message.keyActive && status == PlayerStatus.INACTIVE)
                PlayerManager.setPlayerStatus(playerName, PlayerStatus.ACTIVE)
            else if (!message.keyActive && status == PlayerStatus.ACTIVE)
                PlayerManager.setPlayerStatus(playerName, PlayerStatus.INACTIVE)
        }
    }
}