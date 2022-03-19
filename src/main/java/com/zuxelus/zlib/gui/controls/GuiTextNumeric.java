package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class GuiTextNumeric extends TextFieldWidget {
	int max = Integer.MAX_VALUE;

	public GuiTextNumeric(FontRenderer font, int x, int y, int width, int height, String msg, int max) {
		super(font, x, y, width, height, msg);
		this.max = max; 
	}

	@Override
	public void writeText(String textToWrite) {
		String oldValue = getText();
		super.writeText(textToWrite);
		String fixed = getText().replaceAll("[^\\d]", "");
		if (!fixed.isEmpty()) {
			int newValue = Integer.parseInt(fixed);
			if (newValue > max)
				fixed = oldValue;
		}
		setText(fixed);
	}
}
