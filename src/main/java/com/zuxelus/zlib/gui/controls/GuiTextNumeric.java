package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;

public class GuiTextNumeric extends TextFieldWidget {
	int max = Integer.MAX_VALUE;

	public GuiTextNumeric(FontRenderer font, int x, int y, int width, int height, ITextComponent msg, int max) {
		super(font, x, y, width, height, msg);
		this.max = max; 
	}

	@Override
	public void insertText(String textToWrite) {
		String oldValue = getValue();
		super.insertText(textToWrite);
		String fixed = getValue().replaceAll("[^\\d]", "");
		if (!fixed.isEmpty()) {
			int newValue = Integer.parseInt(fixed);
			if (newValue > max)
				fixed = oldValue;
		}
		setValue(fixed);
	}
}
