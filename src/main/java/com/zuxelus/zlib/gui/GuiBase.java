package com.zuxelus.zlib.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
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
	public void render(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);
		context.pose().pushPose();
		context.pose().translate(guiLeft, guiTop, 0);
		drawGuiContainerForegroundLayer(context, mouseX, mouseY);
		context.pose().popPose();
	}

	@Override
	public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float partialTicks) {
		super.renderBackground(context, mouseX, mouseY, partialTicks);
		drawGuiContainerBackgroundLayer(context, partialTicks, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(GuiGraphics context, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(GuiGraphics context, float partialTicks, int mouseX, int mouseY) {
		context.blit(texture, guiLeft, guiTop, 0, 0, xSize, ySize);
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

	protected void drawTitle(GuiGraphics context) {
		FormattedCharSequence ireorderingprocessor = title.getVisualOrderText();
		context.drawString(font, ireorderingprocessor, (xSize - font.width(ireorderingprocessor)) / 2, 6, 0x404040, false);
	}
}
