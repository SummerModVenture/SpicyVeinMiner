package com.spicymemes.veinminer.client.gui

import net.minecraft.client.gui.screen.*
import net.minecraft.client.gui.widget.button.*
import net.minecraft.client.gui.widget.list.AbstractList
import net.minecraft.client.resources.*
import net.minecraft.util.text.*

class GuiConfig(private val parent: Screen) : Screen(TranslationTextComponent("gui.spicyminer.configuration")) {

    override fun init() {
        addButton(Button(width / 2 - 50, height / 2 + 20, 100, 20, I18n.format("gui.spicyminer.done")) {
            minecraft!!.displayGuiScreen(parent)
        })
    }

    override fun render(x: Int, y: Int, partialTicks: Float) {
        renderBackground()
        drawCenteredString(font, title.formattedText, width / 2, height / 3, 16777215)
        super.render(x, y, partialTicks)
    }
}