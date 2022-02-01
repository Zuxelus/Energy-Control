package com.zuxelus.zlib.gui.controls;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiTextArea extends AbstractWidget {
	private final int lineCount;
	private int maxStringLength = 32;
	private int cursorCounter;
	private int cursorPosition = 0;
	private int cursorLine = 0;
	private String[] text;

	private final Font fontRenderer;

	public GuiTextArea(Font fontRenderer, int xPos, int yPos, int width, int height, int lineCount) {
		super(xPos, yPos, width, height, TextComponent.EMPTY);
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
	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		fill(matrixStack, x - 1, y - 1, x + width + 1, y + height + 1, 0xFFA0A0A0);
		fill(matrixStack, x, y, x + width, y + height, 0xFF000000);
		int textColor = 0xE0E0E0;

		int textLeft = x + 4;
		int textTop = y + (height - lineCount * (fontRenderer.lineHeight + 1)) / 2;

		for (int i = 0; i < lineCount; i++)
			fontRenderer.drawShadow(matrixStack, text[i], textLeft, textTop + (fontRenderer.lineHeight + 1) * i, textColor);
		textTop += (fontRenderer.lineHeight + 1) * cursorLine;
		int cursorPositionX = textLeft + fontRenderer.width(text[cursorLine].substring(0, Math.min(text[cursorLine].length(), cursorPosition))) - 1;
		boolean drawCursor = isFocused() && cursorCounter / 6 % 2 == 0;
		if (drawCursor)
			drawCursorVertical(cursorPositionX, textTop - 1, cursorPositionX + 1, textTop + 1 + fontRenderer.lineHeight);
	}

	// Copy of TextFieldWidget.renderHighlight
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

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShaderColor(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
		bufferbuilder.vertex(left, bottom, 0.0D).endVertex();
		bufferbuilder.vertex(right, bottom, 0.0D).endVertex();
		bufferbuilder.vertex(right, top, 0.0D).endVertex();
		bufferbuilder.vertex(left, top, 0.0D).endVertex();
		tesselator.end();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
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
		String filteredText = SharedConstants.filterText(additionalText);
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
		boolean flag = mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY < (y + height);
		if (isFocused() && flag && mouseButton == 0) {
			int xi = Mth.floor(mouseX) - x;
			int yi = Mth.floor(mouseY) - y;
			setCursorPosition(fontRenderer.plainSubstrByWidth(text[(yi - 4) / 10], xi).length(), (yi - 4) / 10);
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
		if (isFocused() && SharedConstants.isAllowedChatCharacter(typedChar)) {
			writeText(Character.toString(typedChar));
			return true;
		}
		return false;
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
		// TODO Auto-generated method stub
		
	}
}
