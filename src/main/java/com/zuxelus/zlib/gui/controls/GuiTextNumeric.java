package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextNumeric extends GuiTextField {

	public GuiTextNumeric(FontRenderer font, int x, int y, int width, int height) {
		super(font, x, y, width, height);
	}

	@Override
	public void writeText(String textToWrite) {
		super.writeText(textToWrite);
		String fixed = getText().replaceAll("[^\\d]", "");
		setText(fixed);
	}
}
