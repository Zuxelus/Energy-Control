package com.zuxelus.zlib.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
		super(new TranslationTextComponent(name));
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) guiLeft, (float) guiTop, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}

	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {}

	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(texture);
		blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected TextFieldWidget addTextFieldWidget(int left, int top, int width, int height, boolean isEnabled, String text) {
		TextFieldWidget textBox = new TextFieldWidget(font, guiLeft + left, guiTop + top, width, height, null, StringTextComponent.EMPTY);
		textBox.setEditable(isEnabled);
		textBox.changeFocus(isEnabled);
		textBox.setValue(text);
		children.add(textBox);
		setInitialFocus(textBox);
		return textBox;
	}

	protected void drawTitle(MatrixStack matrixStack) {
		IReorderingProcessor ireorderingprocessor = title.getVisualOrderText();
		font.draw(matrixStack, ireorderingprocessor, (xSize - font.width(ireorderingprocessor)) / 2, 6, 0x404040);
	}
}
