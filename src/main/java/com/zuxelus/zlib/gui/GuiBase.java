package com.zuxelus.zlib.gui;

import net.minecraft.client.renderer.RenderPipelines;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

@Environment(EnvType.CLIENT)
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
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		// Screen.extractRenderState draws the widgets; the background comes from extractBackground
		super.extractRenderState(context, mouseX, mouseY, partialTicks);
		context.pose().pushMatrix();
		context.pose().translate(guiLeft, guiTop);
		drawGuiContainerForegroundLayer(context, mouseX, mouseY);
		context.pose().popMatrix();
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float partialTicks) {
		super.extractBackground(context, mouseX, mouseY, partialTicks);
		drawGuiContainerBackgroundLayer(context, partialTicks, mouseX, mouseY);
	}

	protected void drawGuiContainerForegroundLayer(GuiGraphicsExtractor context, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(GuiGraphicsExtractor context, float partialTicks, int mouseX, int mouseY) {
		context.blit(RenderPipelines.GUI_TEXTURED, texture, guiLeft, guiTop, 0, 0, xSize, ySize, 256, 256);
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

	protected void drawTitle(GuiGraphicsExtractor context) {
		FormattedCharSequence ireorderingprocessor = title.getVisualOrderText();
		context.text(font, ireorderingprocessor, (xSize - font.width(ireorderingprocessor)) / 2, 6, 0xFF404040, false);
	}
}
