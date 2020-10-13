package com.zuxelus.energycontrol.screen.controls;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CompactButton extends ButtonWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");

	public CompactButton(int x, int y, int width, int height, Text text, ButtonWidget.PressAction onPress) {
		super(x, y, width, height, text, onPress);
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int i = getYImage(this.isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		drawTexture(matrices, x, y, 0, 64 + i * 12, width / 2 + width % 2, height);
		drawTexture(matrices, x + width / 2 + width % 2, y, 200 - width / 2, 64 + i * 12, width / 2, height);
		renderBg(matrices, mc, mouseX, mouseY);
		mc.textRenderer.draw(matrices, getMessage(), x + (width - mc.textRenderer.getWidth(getMessage())) / 2, y + 2, 0x404040);
	}
}
