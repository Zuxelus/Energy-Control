package com.zuxelus.zlib.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class GuiBase extends Screen {
	protected Identifier texture;
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	public GuiBase(String name, int xSize, int ySize, String texture) {
		super(new TranslatableText(name));
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		RenderSystem.disableDepthTest();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		MatrixStack posestack = RenderSystem.getModelViewStack();
		posestack.push();
		posestack.translate((float) guiLeft, (float) guiTop, 0.0F);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		posestack.pop();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
	}

	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexture(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected TextFieldWidget addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		TextFieldWidget textBox = new TextFieldWidget(textRenderer, guiLeft + left, guiTop + top, width, height, null, LiteralText.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.changeFocus(isEnabled);
		textBox.setText(text);
		addSelectableChild(textBox);
		setInitialFocus(textBox);
		return textBox;
	}

	protected void drawTitle(MatrixStack matrixStack) {
		OrderedText ireorderingprocessor = title.asOrderedText();
		textRenderer.draw(matrixStack, ireorderingprocessor, (xSize - textRenderer.getWidth(ireorderingprocessor)) / 2, 6, 0x404040);
	}
}
