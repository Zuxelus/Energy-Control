package com.zuxelus.zlib.gui.controls;

import net.fabricmc.api.EnvType;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

@Environment(EnvType.CLIENT)
public class GuiTextArea extends AbstractWidget {
	private final int lineCount;
	private int maxStringLength = 32;
	private int cursorCounter;
	private int cursorPosition = 0;
	private int cursorLine = 0;
	private String[] text;

	private final Font fontRenderer;

	public GuiTextArea(Font fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
		super(xPos, yPos, width, height, CommonComponents.EMPTY);
		this.fontRenderer = fontRenderer;
		this.lineCount = lineCount;
		text = new String[lineCount];
		for (int i = 0; i < lineCount; i++)
			text[i] = "";
	}

	public String[] getText() {
		return text;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		context.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0xFFA0A0A0);
		context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);
		int textColor = 0xFFE0E0E0;

		int textLeft = getX() + 4;
		int textTop = getY() + (height - lineCount * (fontRenderer.lineHeight + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			context.text(fontRenderer, text[i], textLeft, textTop + (fontRenderer.lineHeight + 1) * i, textColor);
		textTop += (fontRenderer.lineHeight + 1) * cursorLine;
		int cursorPositionX = textLeft + fontRenderer.width(text[cursorLine].substring(0, Math.min(text[cursorLine].length(), cursorPosition))) - 1;
		boolean drawCursor = isFocused() && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			// same inverting highlight that EditBox uses for selections
			context.textHighlight(cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.lineHeight, true);
	}

	// since SDL input the game only sends typed characters while text input is started, like EditBox does
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		Minecraft.getInstance().onTextInputFocusChange(this, focused);
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
		String filteredText = StringUtil.filterText(additionalText);
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

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int mouseButton = event.button();
		boolean flag = mouseX >= getX() && mouseX < (getX() + width) && mouseY >= getY() && mouseY < (getY() + height);
		if (!flag || mouseButton != InputConstants.MOUSE_BUTTON_LEFT)
			return false;
		// returning true lets the screen focus this widget, so a click into an unfocused area starts typing
		int xi = Mth.floor(mouseX) - getX();
		int yi = Mth.floor(mouseY) - getY();
		int line = Mth.clamp((yi - 4) / 10, 0, lineCount - 1);
		setCursorPosition(fontRenderer.plainSubstrByWidth(text[line], xi).length(), line);
		return true;
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		int keyCode = event.key();
		if (!isFocused())
			return false;
		switch (keyCode) {
		/*case 1:
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;*/
		case InputConstants.KEY_RETURN: // enter
		case InputConstants.KEY_NUMPADENTER:
			setCursorLine(1);
			return true;
		case InputConstants.KEY_BACKSPACE: // backspace
			deleteFromCursor(-1);
			return true;
		case InputConstants.KEY_HOME: //home
			setCursorPosition(0, cursorLine);
			return true;
		case InputConstants.KEY_LEFT: // left
			setCursorPosition(cursorPosition - 1, cursorLine);
			return true;
		case InputConstants.KEY_RIGHT: // right
			setCursorPosition(cursorPosition + 1, cursorLine);
			return true;
		case InputConstants.KEY_UP: // up
			setCursorLine(-1);
			return true;
		case InputConstants.KEY_DOWN: // down
			setCursorLine(1);
			return true;
		case InputConstants.KEY_END: // end
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;
		case InputConstants.KEY_DELETE: // delete
			deleteFromCursor(1);
			return true;
		}
		// not consumed, so the character of the key reaches charTyped
		return false;
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		char typedChar = (char) event.codepoint();
		if (isFocused() && StringUtil.isAllowedChatCharacter(typedChar)) {
			writeText(Character.toString(typedChar));
			return true;
		}
		return false;
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput var1) {
		// TODO Auto-generated method stub
	}
}
