package com.spicymemes.veinminer.client.gui

import com.mojang.blaze3d.vertex.*
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.screens.*
import net.minecraft.network.chat.*

class GuiConfig(private val parent: Screen) : Screen(TranslatableComponent("gui.spicyminer.configuration")) {

    override fun init() {
        addWidget(Button(width / 2 - 50, height / 2 + 20, 100, 20, TranslatableComponent("gui.spicyminer.done")) {
            minecraft!!.forceSetScreen(parent)
        })
    }

    override fun render(matrixStack: PoseStack, x: Int, y: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        drawCenteredString(matrixStack, font, title, width / 2, height / 3, 16777215)
        super.render(matrixStack, x, y, partialTicks)
    }
}