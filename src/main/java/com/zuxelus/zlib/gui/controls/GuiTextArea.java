package com.zuxelus.zlib.gui.controls;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

public class GuiTextArea extends AbstractWidget {
	private static final int MAX_STRING_LENGTH = 128;
	private static final int CELL_WIDTH = 6;
	private static final int TEXT_MARGIN = 4;

	private final int lineCount;
	private final int maxGuiLength; // characters that fit into the widget
	private int cursorCounter;
	private int cursorLine;
	private int caret; // the moving end of the selection, where the text cursor is
	private int anchor; // the fixed end of the selection, equal to caret when nothing is selected
	private int scrollOffset; // first visible character
	private String[] text;

	private final Font fontRenderer;

	public GuiTextArea(Font fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
		super(xPos, yPos, width, height, CommonComponents.EMPTY);
		this.fontRenderer = fontRenderer;
		this.lineCount = lineCount;
		maxGuiLength = (width - TEXT_MARGIN - 2) / CELL_WIDTH;
		text = new String[lineCount];
		for (int i = 0; i < lineCount; i++)
			text[i] = "";
	}

	public String[] getText() {
		return text;
	}

	public void updateCursorCounter() {
		cursorCounter++;
	}

	// the lines can be replaced from outside
	private void clampCursor() {
		int length = text[cursorLine].length();
		caret = Mth.clamp(caret, 0, length);
		anchor = Mth.clamp(anchor, 0, length);
		scrollOffset = Mth.clamp(scrollOffset, 0, Math.max(0, length));
	}

	private boolean hasSelection() {
		return caret != anchor;
	}

	private int selectionStart() {
		return Math.min(caret, anchor);
	}

	private int selectionEnd() {
		return Math.max(caret, anchor);
	}

	public String getSelectedText() {
		clampCursor();
		return text[cursorLine].substring(selectionStart(), selectionEnd());
	}

	private void scrollToCursor() {
		if (caret < scrollOffset)
			scrollOffset = caret;
		else if (caret - scrollOffset > maxGuiLength)
			scrollOffset = caret - maxGuiLength;
		scrollOffset = Math.max(0, scrollOffset);
	}

	private void moveCursor(int position, boolean select) {
		caret = Mth.clamp(position, 0, text[cursorLine].length());
		if (!select)
			anchor = caret;
		scrollToCursor();
	}

	public void setCursorPosition(int x, int y) {
		cursorLine = Mth.clamp(y, 0, lineCount - 1);
		moveCursor(x, false);
	}

	private void setCursorLine(int delta) {
		cursorLine = Mth.clamp(cursorLine + delta, 0, lineCount - 1);
		moveCursor(caret, false);
	}

	// start of the next word to the right, or of the current one to the left
	private int wordBoundary(int position, int direction) {
		String line = text[cursorLine];
		if (direction < 0) {
			while (position > 0 && line.charAt(position - 1) == ' ')
				position--;
			while (position > 0 && line.charAt(position - 1) != ' ')
				position--;
		} else {
			while (position < line.length() && line.charAt(position) != ' ')
				position++;
			while (position < line.length() && line.charAt(position) == ' ')
				position++;
		}
		return position;
	}

	private void deleteRange(int from, int to) {
		String line = text[cursorLine];
		text[cursorLine] = line.substring(0, from) + line.substring(to);
		caret = anchor = from;
		scrollToCursor();
	}

	private void deleteSelection() {
		deleteRange(selectionStart(), selectionEnd());
	}

	private void deleteBackward(boolean word) {
		if (hasSelection())
			deleteSelection();
		else if (caret == 0)
			joinWithPreviousLine();
		else
			deleteRange(word ? wordBoundary(caret, -1) : caret - 1, caret);
	}

	private void deleteForward(boolean word) {
		if (hasSelection())
			deleteSelection();
		else if (caret >= text[cursorLine].length())
			joinWithNextLine();
		else
			deleteRange(caret, word ? wordBoundary(caret, 1) : caret + 1);
	}

	// moves the text right of the cursor to the next line, pushing the lines below down, and puts the cursor at the start of it
	private void splitLine() {
		if (hasSelection())
			deleteSelection();
		if (cursorLine >= lineCount - 1)
			return;
		// the last line would be pushed out, so it is only done when that loses nothing
		if (text[lineCount - 1].isEmpty()) {
			String line = text[cursorLine];
			for (int i = lineCount - 1; i > cursorLine + 1; i--)
				text[i] = text[i - 1];
			text[cursorLine] = line.substring(0, caret);
			text[cursorLine + 1] = line.substring(caret);
		}
		cursorLine++;
		scrollOffset = 0;
		caret = anchor = 0;
	}

	// appends the next line to the current one and moves the lines below it one line up, nothing happens if the result is too long
	private void joinWithNextLine() {
		if (cursorLine >= lineCount - 1 || text[cursorLine].length() + text[cursorLine + 1].length() > MAX_STRING_LENGTH)
			return;
		text[cursorLine] += text[cursorLine + 1];
		for (int i = cursorLine + 1; i < lineCount - 1; i++)
			text[i] = text[i + 1];
		text[lineCount - 1] = "";
	}

	// appends the current line to the previous one, the cursor goes to the join point
	private void joinWithPreviousLine() {
		if (cursorLine == 0 || text[cursorLine - 1].length() + text[cursorLine].length() > MAX_STRING_LENGTH)
			return;
		cursorLine--;
		caret = anchor = text[cursorLine].length();
		joinWithNextLine();
		scrollToCursor();
	}

	public void writeText(String additionalText) {
		clampCursor();
		if (hasSelection())
			deleteSelection();
		String filteredText = StringUtil.filterText(additionalText);
		String line = text[cursorLine];
		int freeCharCount = MAX_STRING_LENGTH - line.length();
		if (freeCharCount <= 0 || filteredText.isEmpty())
			return;
		if (filteredText.length() > freeCharCount)
			filteredText = filteredText.substring(0, freeCharCount);
		text[cursorLine] = line.substring(0, caret) + filteredText + line.substring(caret);
		caret = anchor = caret + filteredText.length();
		scrollToCursor();
	}

	private int textTop() {
		return getY() + (height - lineCount * (fontRenderer.lineHeight + 1)) / 2;
	}

	@Override
	protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		clampCursor();
		context.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, ARGB32.opaque(0xA0A0A0));
		context.fill(getX(), getY(), getX() + width, getY() + height, ARGB32.opaque(0x000000));
		int textColor = 0xE0E0E0;

		int textLeft = getX() + TEXT_MARGIN;
		int lineHeight = fontRenderer.lineHeight + 1;
		int textTop = textTop();

		for (int i = 0; i < lineCount; i++) {
			String line = text[i];
			int visible = Math.min(line.length() - scrollOffset, maxGuiLength);
			for (int j = 0; j < visible; j++) {
				String character = line.substring(scrollOffset + j, scrollOffset + j + 1);
				context.drawString(fontRenderer, character, textLeft + j * CELL_WIDTH + (CELL_WIDTH - fontRenderer.width(character)) / 2, textTop + lineHeight * i, textColor);
			}
		}

		int lineTop = textTop + lineHeight * cursorLine;
		// same inverting highlight that EditBox uses for selections
		if (isFocused() && hasSelection()) {
			int from = Math.max(selectionStart(), scrollOffset);
			int to = Math.min(selectionEnd(), scrollOffset + maxGuiLength);
			if (from < to)
				drawHighlight(context, textLeft + (from - scrollOffset) * CELL_WIDTH - 1, lineTop - 1, textLeft + (to - scrollOffset) * CELL_WIDTH - 1, lineTop + 1 + fontRenderer.lineHeight);
		}
		if (isFocused() && cursorCounter / 6 % 2 == 0) {
			int cursorX = textLeft + (caret - scrollOffset) * CELL_WIDTH - 1;
			drawHighlight(context, cursorX, lineTop - 1, cursorX + 1, lineTop + 1 + fontRenderer.lineHeight);
		}
	}

	// copy of EditBox.renderHighlight
	private void drawHighlight(GuiGraphics context, int left, int top, int right, int bottom) {
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
		context.fill(RenderType.guiTextHighlight(), left, top, right, bottom, -16776961);
	}

	private int lineAt(double mouseY) {
		return Mth.clamp((int) Math.floor((mouseY - textTop()) / (fontRenderer.lineHeight + 1)), 0, lineCount - 1);
	}

	private int columnAt(double mouseX) {
		int column = scrollOffset + (int) Math.floor((mouseX - getX() - TEXT_MARGIN + CELL_WIDTH / 2.0) / CELL_WIDTH);
		return Mth.clamp(column, 0, text[cursorLine].length());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean inside = mouseX >= getX() && mouseX < (getX() + width) && mouseY >= getY() && mouseY < (getY() + height);
		if (!inside || button != InputConstants.MOUSE_BUTTON_LEFT)
			return false;
		// returning true lets the screen focus this widget, so a click into an unfocused area starts typing
		cursorLine = lineAt(mouseY);
		moveCursor(columnAt(mouseX), Screen.hasShiftDown());
		return true;
	}

	// selecting with the mouse
	@Override
	protected void onDrag(double mouseX, double mouseY, double dx, double dy) {
		moveCursor(columnAt(mouseX), true);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!isFocused())
			return false;
		clampCursor();
		if (Screen.isSelectAll(keyCode)) {
			anchor = 0;
			caret = text[cursorLine].length();
			scrollToCursor();
			return true;
		}
		if (Screen.isCopy(keyCode)) {
			Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
			return true;
		}
		if (Screen.isPaste(keyCode)) {
			writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
			return true;
		}
		if (Screen.isCut(keyCode)) {
			Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
			if (hasSelection())
				deleteSelection();
			return true;
		}

		boolean shift = Screen.hasShiftDown();
		boolean ctrl = Screen.hasControlDown();
		switch (keyCode) {
		case InputConstants.KEY_RETURN:
		case InputConstants.KEY_NUMPADENTER:
			splitLine();
			return true;
		case InputConstants.KEY_BACKSPACE:
			deleteBackward(ctrl);
			return true;
		case InputConstants.KEY_DELETE:
			deleteForward(ctrl);
			return true;
		case InputConstants.KEY_HOME:
			moveCursor(0, shift);
			return true;
		case InputConstants.KEY_END:
			moveCursor(text[cursorLine].length(), shift);
			return true;
		case InputConstants.KEY_LEFT:
			moveCursor(ctrl ? wordBoundary(caret, -1) : caret - 1, shift);
			return true;
		case InputConstants.KEY_RIGHT:
			moveCursor(ctrl ? wordBoundary(caret, 1) : caret + 1, shift);
			return true;
		case InputConstants.KEY_UP:
			setCursorLine(-1);
			return true;
		case InputConstants.KEY_DOWN:
			setCursorLine(1);
			return true;
		}
		// not consumed, so the character of the key reaches charTyped
		return false;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (isFocused() && StringUtil.isAllowedChatCharacter(codePoint)) {
			writeText(Character.toString(codePoint));
			return true;
		}
		return false;
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) { }
}
