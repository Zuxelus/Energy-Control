package com.zuxelus.zlib.gui.controls;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTextArea extends Gui {
	private final static int maxStringLength = 128;
	private final static int maxGuiLength = 39;

	private final int lineCount;
	private int cursorCounter;
	private int cursorPosition;
	private int cursorLine;
	private int lineScrollOffset;
	private int selectionEnd;
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

	public void updateCursorCounter() {
		cursorCounter++;
	}

	public String[] getText() {
		return text;
	}

	public String getSelectedText() {
		int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		return text[cursorLine].substring(i, j);
	}

	public void writeText(String textToWrite) {
		String newLine = "";
		String filteredText = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
		int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
		int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
		int freeCharCount = maxStringLength - text[cursorLine].length() - (i - j);

		if (text[cursorLine].length() > 0)
			newLine = newLine + text[cursorLine].substring(0, i);

		int l;
		if (freeCharCount < filteredText.length()) {
			newLine = newLine + filteredText.substring(0, freeCharCount);
			l = freeCharCount;
		} else {
			newLine = newLine + filteredText;
			l = filteredText.length();
		}

		if (text[cursorLine].length() > 0 && j < text[cursorLine].length())
			newLine = newLine + text[cursorLine].substring(j);

		text[cursorLine] = newLine;
		moveCursorBy(i - selectionEnd + l);
	}

	public void deleteWords(int num) {
		if (text[cursorLine].isEmpty())
			return;

		if (selectionEnd != cursorPosition)
			writeText("");
		else
			deleteFromCursor(getNthWordFromCursor(num) - cursorPosition);

	}

	public void deleteFromCursor(int count) {
		if (text[cursorLine].isEmpty())
			return;

		if (selectionEnd != cursorPosition) {
			writeText("");
			return;
		}

		boolean back = count < 0;
		int left = back ? cursorPosition + count : cursorPosition;
		int right = back ? cursorPosition : cursorPosition + count;
		String newLine = "";

		if (left >= 0)
			newLine = text[cursorLine].substring(0, left);
		if (right < text[cursorLine].length())
			newLine = newLine + text[cursorLine].substring(right);
		text[cursorLine] = newLine;
		if (back)
			moveCursorBy(count);
	}

	/**
	 * Gets the starting index of the word at the specified number of words away from the cursor position.
	 */
	public int getNthWordFromCursor(int numWords) {
		return getNthWordFromPos(numWords, cursorPosition);
	}

	/**
	 * Gets the starting index of the word at a distance of the specified number of words away from the given position.
	 */
	public int getNthWordFromPos(int n, int pos) {
		return getNthWordFromPosWS(n, pos, true);
	}

	/**
	 * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
	 */
	public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		int i = pos;
		boolean flag = n < 0;
		int j = Math.abs(n);

		for (int k = 0; k < j; ++k)
			if (!flag) {
				int l = text[cursorLine].length();
				i = text[cursorLine].indexOf(32, i);

				if (i == -1)
					i = l;
				else
					while (skipWs && i < l && text[cursorLine].charAt(i) == ' ')
						++i;
			} else {
				while (skipWs && i > 0 && text[cursorLine].charAt(i - 1) == ' ')
					--i;
				while (i > 0 && text[cursorLine].charAt(i - 1) != ' ')
					--i;
			}
		return i;
	}

	private void setCursorLine(int delta) {
		int newCursorLine = cursorLine + delta;
		if (newCursorLine < 0)
			newCursorLine = 0;
		if (newCursorLine >= lineCount)
			newCursorLine = lineCount - 1;
		cursorPosition = Math.min(selectionEnd, text[newCursorLine].length());
		setSelectionPos(cursorPosition);
		cursorLine = newCursorLine;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the selection
	 */
	public void moveCursorBy(int num) {
		setCursorPosition(selectionEnd + num, cursorLine);
	}

	/**
	 * Sets the current position of the cursor.
	 */
	public void setCursorPosition(int x, int y) {
		if (y >= text.length)
			y = text.length - 1;
		cursorPosition = x;
		cursorLine = y;

		int lineLength = text[y].length();
		cursorPosition = MathHelper.clamp(cursorPosition, 0, lineLength);
		setSelectionPos(cursorPosition);
	}

	public void setCursorPositionZero() {
		setCursorPosition(0, cursorLine);
	}

	public void setCursorPositionEnd() {
		setCursorPosition(text[cursorLine].length(), cursorLine);
	}

	public boolean textAreaKeyTyped(char typedChar, int keyCode) {
		if (!isFocused)
			return false;

		if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			setCursorPositionEnd();
			setSelectionPos(0);
			return true;
		}
		if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			return true;
		}
		if (GuiScreen.isKeyComboCtrlV(keyCode)) {

			writeText(GuiScreen.getClipboardString());
			return true;
		}
		if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			writeText("");
			return true;
		}

		switch (typedChar) {
		case 1:
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;
		case 13:
			setCursorLine(1);
			return true;
		default:
			switch (keyCode) {
			case Keyboard.KEY_BACK: // backspace
				if (GuiScreen.isCtrlKeyDown())
					deleteWords(-1);
				else
					deleteFromCursor(-1);
				return true;
			case Keyboard.KEY_HOME:
				if (GuiScreen.isShiftKeyDown())
					setSelectionPos(0);
				else
					setCursorPositionZero();
				return true;
			case Keyboard.KEY_LEFT:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown())
						setSelectionPos(getNthWordFromPos(-1, selectionEnd));
					else
						setSelectionPos(selectionEnd - 1);
				} else if (GuiScreen.isCtrlKeyDown())
					setCursorPosition(getNthWordFromCursor(-1), cursorLine);
				else
					moveCursorBy(-1);
				return true;
			case Keyboard.KEY_RIGHT:
				if (GuiScreen.isShiftKeyDown()) {
					if (GuiScreen.isCtrlKeyDown())
						setSelectionPos(getNthWordFromPos(1, selectionEnd));
					else
						setSelectionPos(selectionEnd + 1);
				} else if (GuiScreen.isCtrlKeyDown())
					setCursorPosition(getNthWordFromCursor(1), cursorLine);
				else
					moveCursorBy(1);
				return true;
			case Keyboard.KEY_UP:
				setCursorLine(-1);
				return true;
			case Keyboard.KEY_DOWN:
				setCursorLine(1);
				return true;
			case Keyboard.KEY_END:
				if (GuiScreen.isShiftKeyDown())
					setSelectionPos(text[cursorLine].length());
				else
					setCursorPositionEnd();
				return true;
			case Keyboard.KEY_DELETE:
				if (GuiScreen.isCtrlKeyDown())
					deleteWords(1);
				else
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

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		isFocused = mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height;
		if (isFocused && mouseButton == 0) {
			int xi = MathHelper.floor(mouseX) - xPos;
			int yi = MathHelper.floor(mouseY) - yPos;
			setCursorPosition(xi / 6, (yi - 4) / 10);
		}
	}

	public void drawTextBox() {
		drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, 0xFFA0A0A0);
		drawRect(xPos, yPos, xPos + width, yPos + height, 0xFF000000);
		int textColor = 0xE0E0E0;

		int textLeft = xPos + 4;
		int textTop = yPos + (height - lineCount * (fontRenderer.FONT_HEIGHT + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			if (text[i].length() > lineScrollOffset)
				for (int j = 0; j < Math.min(text[i].length() - lineScrollOffset, maxGuiLength); j++) {
					String line = text[i].substring(j + lineScrollOffset, j + lineScrollOffset + 1);
					fontRenderer.drawStringWithShadow(line, textLeft + j * 6 + (6 - fontRenderer.getStringWidth(line)) / 2, textTop + (fontRenderer.FONT_HEIGHT + 1) * i, textColor);
				}

		textTop += (fontRenderer.FONT_HEIGHT + 1) * cursorLine;
		int cursorPositionX = textLeft + selectionEnd * 6 - 1 - lineScrollOffset * 6;
		boolean drawCursor = isFocused && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			drawCursorVertical(cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.FONT_HEIGHT);
		int selectionPositionX = textLeft + cursorPosition * 6 - 1 - lineScrollOffset * 6;
		drawSelectionBox(cursorPositionX, textTop - 1, Math.max(selectionPositionX, xPos), textTop + 1 + fontRenderer.FONT_HEIGHT);
	}

	// Copy of GuiTextField.drawSelectionBox
	private void drawSelectionBox(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > xPos + width) {
			endX = xPos + width;
		}

		if (startX > xPos + width) {
			startX = xPos + width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(startX, endY, 0.0D).endVertex();
		bufferbuilder.pos(endX, endY, 0.0D).endVertex();
		bufferbuilder.pos(endX, startY, 0.0D).endVertex();
		bufferbuilder.pos(startX, startY, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
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

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(left, bottom, 0.0D).endVertex();
		bufferbuilder.pos(right, bottom, 0.0D).endVertex();
		bufferbuilder.pos(right, top, 0.0D).endVertex();
		bufferbuilder.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setSelectionPos(int position) {
		int i = text[cursorLine].length();
		if (position > i)
			position = i;
		if (position < 0)
			position = 0;
		selectionEnd = position;

		if (position - lineScrollOffset > maxGuiLength)
			lineScrollOffset += position - lineScrollOffset - maxGuiLength;
		if (position - lineScrollOffset < 0 && lineScrollOffset > 0)
			lineScrollOffset += position - lineScrollOffset;
	}
}
