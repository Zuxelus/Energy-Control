package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextNumeric extends GuiTextField {
	int max = Integer.MAX_VALUE;

	public GuiTextNumeric(int id, FontRenderer font, int x, int y, int width, int height, int max) {
		super(id, font, x, y, width, height);
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
