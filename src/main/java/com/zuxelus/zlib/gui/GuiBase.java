package com.zuxelus.zlib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
		this.texture = new ResourceLocation(texture);
	}

	@Override
	public void init() {
		super.init();
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	@Override
	public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		RenderSystem.disableDepthTest();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((float) guiLeft, (float) guiTop, 0.0F);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
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
		//textBox.changeFocus(isEnabled);
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
