package com.zuxelus.zlib.gui;

import net.minecraft.text.Text;
import net.minecraft.screen.ScreenTexts;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class GuiBase extends Screen {
	protected Identifier texture;
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	public GuiBase(String name, int xSize, int ySize, String texture) {
		super(Text.translatable(name));
		this.xSize = xSize;
		this.ySize = ySize;
		this.texture = new Identifier(texture);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		// Screen.render draws the background (see renderBackground) and then the widgets
		super.render(context, mouseX, mouseY, partialTicks);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate((float) guiLeft, (float) guiTop, 0.0F);
		drawGuiContainerForegroundLayer(context, mouseX, mouseY);
		matrices.pop();
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(context, mouseX, mouseY, partialTicks);
		drawGuiContainerBackgroundLayer(context, partialTicks, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(DrawContext context, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(DrawContext context, float partialTicks, int mouseX, int mouseY) {
		context.drawTexture(texture, guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	protected TextFieldWidget addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		TextFieldWidget textBox = new TextFieldWidget(textRenderer, guiLeft + left, guiTop + top, width, height, null, ScreenTexts.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.setFocused(isEnabled);
		textBox.setText(text);
		addSelectableChild(textBox);
		setInitialFocus(textBox);
		return textBox;
	}

	protected void drawTitle(DrawContext context) {
		OrderedText ireorderingprocessor = title.asOrderedText();
		context.drawText(textRenderer, ireorderingprocessor, (xSize - textRenderer.getWidth(ireorderingprocessor)) / 2, 6, 0x404040, false);
	}
}
