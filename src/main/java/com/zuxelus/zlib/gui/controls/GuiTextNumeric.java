package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextNumeric extends TextFieldWidget {

	public GuiTextNumeric(FontRenderer font, int x, int y, int width, int height, String msg) {
		super(font, x, y, width, height, msg);
	}

	@Override
	public void writeText(String textToWrite) {
		super.writeText(textToWrite);
		String fixed = getText().replaceAll("[^\\d]", "");
		setText(fixed);
	}
}
