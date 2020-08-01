package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.NarratorManager;

public class GuiTextArea extends Screen {
	private final int lineCount;
	private int maxStringLength = 32;
	private int cursorCounter;
	private int cursorPosition = 0;
	private int cursorLine = 0;
	private boolean isFocused = false;
	private String[] text;

	private final TextRenderer fontRenderer;

	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;

	public GuiTextArea(TextRenderer fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
		super(NarratorManager.EMPTY);
		this.xPos = xPos;
		this.yPos = yPos;
		this.width = width;
		this.height = height;
		this.fontRenderer = fontRenderer;
		this.lineCount = lineCount;
		text = new String[lineCount];
		for (int i = 0; i < lineCount; i++)
			text[i] = "";
	}

	public String[] getText() {
		return text;
	}

	public void drawTextBox() {
		fill(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, 0xFFA0A0A0);
		fill(xPos, yPos, xPos + width, yPos + height, 0xFF000000);
		int textColor = 0xE0E0E0;

		int textLeft = xPos + 4;
		int textTop = yPos + (height - lineCount * (fontRenderer.fontHeight + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			fontRenderer.drawWithShadow(text[i], textLeft, textTop + (fontRenderer.fontHeight + 1) * i, textColor);
		textTop += (fontRenderer.fontHeight + 1) * cursorLine;
		int cursorPositionX = textLeft + fontRenderer.getStringWidth(text[cursorLine].substring(0, Math.min(text[cursorLine].length(), cursorPosition))) - 1;
		boolean drawCursor = isFocused && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			drawCursorVertical(cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.fontHeight);
	}

	private void drawCursorVertical(int left, int top, int right, int bottom) {
		// TextFieldWidget.drawSelectionHighlight() copy
		if (left < right) {
			int i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			int j = top;
			top = bottom;
			bottom = j;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex(left, bottom, 0.0D).next();
		bufferBuilder.vertex(right, bottom, 0.0D).next();
		bufferBuilder.vertex(right, top, 0.0D).next();
		bufferBuilder.vertex(left, top, 0.0D).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	public void setCursorPosition(int x, int y) {
		if (y >= text.length)
			y = text.length - 1;
		cursorPosition = x;
		cursorLine = y;
		int lineLength = text[y].length();

		if (cursorPosition < 0)
			cursorPosition = 0;

		if (cursorPosition > lineLength)
			cursorPosition = lineLength;
	}

	public void deleteFromCursor(int count) {
		if (text[cursorLine].length() != 0) {
			boolean back = count < 0;
			String curLine = text[cursorLine];
			int left = back ? cursorPosition + count : cursorPosition;
			int right = back ? cursorPosition : cursorPosition + count;
			String newLine = "";

			if (left >= 0)
				newLine = curLine.substring(0, left);

			if (right < curLine.length())
				newLine = newLine + curLine.substring(right);

			text[cursorLine] = newLine;

			if (back)
				setCursorPosition(cursorPosition + count, cursorLine);
		}
	}

	public void writeText(String additionalText) {
		String newLine = "";
		String filteredText = SharedConstants.stripInvalidChars(additionalText);
		int freeCharCount = this.maxStringLength - text[cursorLine].length();

		if (text[cursorLine].length() > 0)
			newLine = newLine + text[cursorLine].substring(0, cursorPosition);

		if (freeCharCount < filteredText.length())
			newLine = newLine + filteredText.substring(0, freeCharCount);
		else
			newLine = newLine + filteredText;

		if (text[cursorLine].length() > 0 && cursorPosition < text[cursorLine].length())
			newLine = newLine + text[cursorLine].substring(cursorPosition);

		text[cursorLine] = newLine;
		setCursorPosition(cursorPosition + filteredText.length(), cursorLine);
	}

	private void setCursorLine(int delta) {
		int newCursorLine = cursorLine + delta;
		if (newCursorLine < 0)
			newCursorLine = 0;
		if (newCursorLine >= lineCount)
			newCursorLine = lineCount - 1;
		cursorPosition = Math.min(cursorPosition, text[newCursorLine].length());
		cursorLine = newCursorLine;
	}

	/*public void mouseClicked(int x, int y, int par3) {
		isFocused = x >= xPos && x < xPos + width && y >= yPos && y < yPos + height;
	}*/

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
	}
	
	public boolean textAreaCharTyped(char typedChar, int keyCode) {
		if (!isFocused)
			return false;

		if (SharedConstants.isValidChar(typedChar)) {
			writeText(Character.toString(typedChar));
			return true;
		}
		return false;
	}

	public boolean textAreaKeyTyped(int keyCode, int scanCode, int modifiers) {
		if (!isFocused)
			return false;

		switch (keyCode) {
		/* case 1: setCursorPosition(text[cursorLine].length(), cursorLine); 
			return true; */
		case 259:// backspace
			deleteFromCursor(-1);
			return true;
		case 261: // KEY_DELETE:
			deleteFromCursor(1);
			return true;
		case 262: // KEY_RIGHT:
			setCursorPosition(cursorPosition + 1, cursorLine);
			return true;
		case 263: // KEY_LEFT:
			setCursorPosition(cursorPosition - 1, cursorLine);
			return true;
		case 268: // KEY_HOME:
			setCursorPosition(0, cursorLine);
			return true;
		case 269: // KEY_END:
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;
		case 265: // KEY_UP:
			setCursorLine(-1);
			return true;
		case 257: // KEY_ENTER
		case 264: // KEY_DOWN:
			setCursorLine(1);
			return true;
		default:
			return false;
		}
	}
}
