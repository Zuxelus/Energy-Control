package com.zuxelus.zlib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public abstract class GuiBase extends Screen {
	protected ResourceLocation texture;
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	public GuiBase(String name, int xSize, int ySize, String texture) {
		super(Component.translatable(name));
		this.xSize = xSize;
		this.ySize = ySize;
		this.texture = ResourceLocation.parse(texture);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.pose().pushPose();
		matrixStack.pose().translate(guiLeft, guiTop, 0);
		drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		matrixStack.pose().popPose();
	}

	@Override
	public void renderBackground(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
		drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(GuiGraphics matrixStack, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY) {
		matrixStack.blit(texture, guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected EditBox addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		EditBox textBox = new EditBox(font, guiLeft + left, guiTop + top, width, height, null, CommonComponents.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.setValue(text);
		addWidget(textBox);
		setInitialFocus(textBox);
		return textBox;
	}

	protected void drawTitle(GuiGraphics matrixStack) {
		FormattedCharSequence ireorderingprocessor = title.getVisualOrderText();
		matrixStack.drawString(font, ireorderingprocessor, (xSize - font.width(ireorderingprocessor)) / 2, 6, 0x404040, false);
	}
}
