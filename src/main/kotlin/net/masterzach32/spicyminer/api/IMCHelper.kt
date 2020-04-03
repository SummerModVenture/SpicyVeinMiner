package net.masterzach32.spicyminer.api

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInterModComms

object IMCHelper {

    private const val modid = "spicyminer"

    internal val messages = listOf("addTool", "addBlockBlacklist")

    @JvmStatic
    fun addTool(type: String, name: ResourceLocation): Boolean {
        val msg = NBTTagCompound().apply {
            setString("type", type)
            setString("name", name.toString())
        }
        return FMLInterModComms.sendMessage(modid, "addTool", msg)
    }

    @JvmStatic
    fun addBlockBlacklist(name: ResourceLocation): Boolean {
        val msg = NBTTagCompound().apply {
            setString("name", name.toString())
        }
        return FMLInterModComms.sendMessage(modid, "addBlockBlacklist", msg)
    }
}