package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class GuiTextNumeric extends TextFieldWidget {

	public GuiTextNumeric(FontRenderer font, int x, int y, int width, int height, ITextComponent msg) {
		super(font, x, y, width, height, msg);
	}

	@Override
	public void insertText(String textToWrite) {
		super.insertText(textToWrite);
		String fixed = getValue().replaceAll("[^\\d]", "");
		setValue(fixed);
	}
}
