package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class GuiTextNumeric extends EditBox {

	public GuiTextNumeric(Font font, int x, int y, int width, int height, Component msg) {
		super(font, x, y, width, height, msg);
	}

	@Override
	public void insertText(String textToWrite) {
		super.insertText(textToWrite);
		String fixed = getValue().replaceAll("[^\\d]", "");
		setValue(fixed);
	}
}
