package com.zuxelus.energycontrol.gui.controls;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.zuxelus.energycontrol.EnergyControl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CompactButton extends ButtonWidget {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/gui/gui_thermal_monitor.png");
	private int id;

	public CompactButton(int id, int x, int y, int widthIn, int heightIn, Text buttonText, ButtonWidget.PressAction onPress) {
		super(x, y, widthIn, heightIn, buttonText, onPress);
		this.id = id;
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (!visible)
			return;

		MinecraftClient minecraft = MinecraftClient.getInstance();
		TextRenderer fontRenderer = minecraft.textRenderer;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.getYImage(isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		drawTexture(matrixStack, x, y, 0, 64 + i * 12, width / 2 + width % 2, height);
		drawTexture(matrixStack, x + width / 2 + width % 2, y, 200 - width / 2, 64 + i * 12, width / 2, height);
		renderBackground(matrixStack, minecraft, mouseX, mouseY);
		OrderedText ireorderingprocessor = getMessage().asOrderedText();
		fontRenderer.draw(matrixStack, ireorderingprocessor, x + (width - fontRenderer.getWidth(ireorderingprocessor)) / 2, y + (height - 8) / 2, 0x404040);
	}

	public int getId() {
		return id;
	}
}
