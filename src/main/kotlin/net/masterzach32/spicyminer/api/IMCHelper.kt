package net.masterzach32.spicyminer.api

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInterModComms

object IMCHelper {

    fun addTool(type: String, name: ResourceLocation): Boolean {
        val msg = NBTTagCompound().apply {
            setString("type", type)
            setString("name", name.toString())
        }
        return FMLInterModComms.sendMessage("spicyminer", "addTool", msg)
    }
}