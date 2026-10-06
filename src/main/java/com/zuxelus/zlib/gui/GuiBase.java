package com.zuxelus.zlib.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

public abstract class GuiBase extends Screen {
	protected Identifier texture;
	protected int xSize = 131;
	protected int ySize = 136;
	protected int guiLeft;
	protected int guiTop;

	public GuiBase(String name, int xSize, int ySize, String texture) {
		super(Component.translatable(name));
		this.xSize = xSize;
		this.ySize = ySize;
		this.texture = Identifier.parse(texture);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractRenderState(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.pose().pushMatrix();
		matrixStack.pose().translate(guiLeft, guiTop);
		drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		matrixStack.pose().popMatrix();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(matrixStack, mouseX, mouseY, partialTicks);
		drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(GuiGraphicsExtractor matrixStack, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(GuiGraphicsExtractor matrixStack, float partialTicks, int mouseX, int mouseY) {
		matrixStack.blit(RenderPipelines.GUI_TEXTURED, texture, guiLeft, guiTop, 0, 0, xSize, ySize, 256, 256);
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

	protected void drawTitle(GuiGraphicsExtractor matrixStack) {
		FormattedCharSequence ireorderingprocessor = title.getVisualOrderText();
		matrixStack.text(font, ireorderingprocessor, (xSize - font.width(ireorderingprocessor)) / 2, 6, ARGB.opaque(0x404040), false);
	}
}
