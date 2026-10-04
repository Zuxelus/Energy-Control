package com.zuxelus.zlib.gui.controls;

import net.minecraft.screen.ScreenTexts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class GuiTextArea extends ClickableWidget {
	private final int lineCount;
	private int maxStringLength = 32;
	private int cursorCounter;
	private int cursorPosition = 0;
	private int cursorLine = 0;
	private String[] text;

	private final TextRenderer fontRenderer;

	public GuiTextArea(TextRenderer fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
		super(xPos, yPos, width, height, ScreenTexts.EMPTY);
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
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		context.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0xFFA0A0A0);
		context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);
		int textColor = 0xE0E0E0;

		int textLeft = getX() + 4;
		int textTop = getY() + (height - lineCount * (fontRenderer.fontHeight + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			context.drawTextWithShadow(fontRenderer, text[i], textLeft, textTop + (fontRenderer.fontHeight + 1) * i, textColor);
		textTop += (fontRenderer.fontHeight + 1) * cursorLine;
		int cursorPositionX = textLeft + fontRenderer.getWidth(text[cursorLine].substring(0, Math.min(text[cursorLine].length(), cursorPosition))) - 1;
		boolean drawCursor = isFocused() && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			// same inverting highlight that TextFieldWidget uses for selections
			context.fill(RenderLayer.getGuiTextHighlight(), cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.fontHeight, 0xFF0000FF);
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

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean flag = mouseX >= getX() && mouseX < (getX() + width) && mouseY >= getY() && mouseY < (getY() + height);
		if (isFocused() && flag && mouseButton == 0) {
			int xi = MathHelper.floor(mouseX) - getX();
			int yi = MathHelper.floor(mouseY) - getY();
			setCursorPosition(fontRenderer.trimToWidth(text[(yi - 4) / 10], xi).length(), (yi - 4) / 10);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!isFocused())
			return false;
		switch (keyCode) {
		/*case 1:
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;*/
		case 257: // enter
		case 335:
			setCursorLine(1);
			return true;
		case 259: // backspace
			deleteFromCursor(-1);
			return true;
		case 268: //home
			setCursorPosition(0, cursorLine);
			return true;
		case 263: // left
			setCursorPosition(cursorPosition - 1, cursorLine);
			return true;
		case 262: // right
			setCursorPosition(cursorPosition + 1, cursorLine);
			return true;
		case 265: // up
			setCursorLine(-1);
			return true;
		case 264: // down
			setCursorLine(1);
			return true;
		case 269: // end
			setCursorPosition(text[cursorLine].length(), cursorLine);
			return true;
		case 261: // delete
			deleteFromCursor(1);
			return true;
		}
		return true;
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if (isFocused() && SharedConstants.isValidChar(typedChar)) {
			writeText(Character.toString(typedChar));
			return true;
		}
		return false;
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder var1) {
		// TODO Auto-generated method stub
	}
}
