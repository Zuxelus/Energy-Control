package com.zuxelus.energycontrol.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class ScreenBase extends Screen {
	protected int containerWidth = 131;
	protected int containerHeight = 136;
	protected int x;
	protected int y;

	protected ScreenBase(Text title, int xSize, int ySize) {
		super(title);
		containerWidth = xSize;
		containerHeight = ySize;
	}

	protected void init() {
		super.init();
		x = (width - containerWidth) / 2;
		y = (height - containerHeight) / 2;
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		drawBackground(matrices, delta, mouseX, mouseY);
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		super.render(matrices, mouseX, mouseY, delta);
		RenderSystem.pushMatrix();
		RenderSystem.translatef((float) x, (float) y, 0.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableRescaleNormal();
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawForeground(matrices, mouseX, mouseY);
		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
	}

	protected abstract void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY);

	protected abstract void drawForeground(MatrixStack matrices, int mouseX, int mouseY);

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
