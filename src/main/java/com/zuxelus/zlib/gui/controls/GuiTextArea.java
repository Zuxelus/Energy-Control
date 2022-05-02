package com.zuxelus.zlib.gui.controls;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class GuiTextArea extends Gui {
	private final int lineCount;
	private int maxStringLength = 32;
	private int cursorCounter;
	private int cursorPosition;
	private int cursorLine;
	private boolean isFocused;
	private String[] text;

	private final FontRenderer fontRenderer;

	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;

	public GuiTextArea(FontRenderer fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
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
		drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, 0xFFA0A0A0);
		drawRect(xPos, yPos, xPos + width, yPos + height, 0xFF000000);
		int textColor = 0xE0E0E0;

		int textLeft = xPos + 4;
		int textTop = yPos + (height - lineCount * (fontRenderer.FONT_HEIGHT + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			fontRenderer.drawStringWithShadow(text[i], textLeft, textTop + (fontRenderer.FONT_HEIGHT + 1) * i, textColor);
		textTop += (fontRenderer.FONT_HEIGHT + 1) * cursorLine;
		int cursorPositionX = textLeft + fontRenderer.getStringWidth(text[cursorLine].substring(0, Math.min(text[cursorLine].length(), cursorPosition))) - 1;
		boolean drawCursor = isFocused && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			drawCursorVertical(cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.FONT_HEIGHT);
	}

	// Copy of GuiTextField.drawSelectionBox
	private void drawCursorVertical(int left, int top, int right, int bottom) {
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

		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_OR_REVERSE);
		tessellator.startDrawingQuads();
		tessellator.addVertex(left, bottom, 0.0D);
		tessellator.addVertex(right, bottom, 0.0D);
		tessellator.addVertex(right, top, 0.0D);
		tessellator.addVertex(left, top, 0.0D);
		tessellator.draw();
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void updateCursorCounter() {
		cursorCounter++;
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
		String filteredText = ChatAllowedCharacters.filerAllowedCharacters(additionalText);
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

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		isFocused = mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height;
		if (isFocused && mouseButton == 0) {
			int xi = MathHelper.floor_float(mouseX) - xPos;
			int yi = MathHelper.floor_float(mouseY) - yPos;
			setCursorPosition(fontRenderer.trimStringToWidth(text[(yi - 4) / 10], xi).length(), (yi - 4) / 10);
		}
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
	}

	public boolean textAreaKeyTyped(char typedChar, int keyCode) {
		if (!isFocused)
			return false;

		switch (typedChar) {
		case 1:
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;
		case 13:
			setCursorLine(1);
			return true;
		default:
			switch (keyCode) {
			case 14:// backspace
				deleteFromCursor(-1);
				return true;
			case Keyboard.KEY_HOME:
				setCursorPosition(0, cursorLine);
				return true;
			case Keyboard.KEY_LEFT:
				setCursorPosition(cursorPosition - 1, cursorLine);
				return true;
			case Keyboard.KEY_RIGHT:
				setCursorPosition(cursorPosition + 1, cursorLine);
				return true;
			case Keyboard.KEY_UP:
				setCursorLine(-1);
				return true;
			case Keyboard.KEY_DOWN:
				setCursorLine(1);
				return true;
			case Keyboard.KEY_END:
				setCursorPosition(text[cursorLine].length(), cursorLine);
				return true;
			case Keyboard.KEY_DELETE:
				deleteFromCursor(1);
				return true;
			default:
				if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
					writeText(Character.toString(typedChar));
					return true;
				}
				return false;
			}
		}
	}
}