package com.zuxelus.zlib.gui.controls;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class GuiTextNumeric extends EditBox {
	int max = Integer.MAX_VALUE;

	public GuiTextNumeric(Font font, int x, int y, int width, int height, Component msg, int max) {
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
