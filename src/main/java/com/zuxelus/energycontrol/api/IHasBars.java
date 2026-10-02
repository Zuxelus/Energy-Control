package com.zuxelus.energycontrol.api;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zuxelus.energycontrol.renderers.ModRenderTypes;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {

	boolean enableBars(ItemStack stack);

	void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack, MultiBufferSource buffer);

	// copy from GuiComponent.fillGradient()
	static void drawTransparentRect(PoseStack matrixStack, MultiBufferSource buffer, float left, float top, float right, float bottom, float zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		Matrix4f matrix = matrixStack.last().pose();
		VertexConsumer builder = buffer.getBuffer(ModRenderTypes.SCREEN_COLOR);
		builder.addVertex(matrix, right, top, zLevel).setColor(f1, f2, f3, f).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, left, top, zLevel).setColor(f1, f2, f3, f).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, left, bottom, zLevel).setColor(f1, f2, f3, f).setLight(LightTexture.FULL_BRIGHT);
		builder.addVertex(matrix, right, bottom, zLevel).setColor(f1, f2, f3, f).setLight(LightTexture.FULL_BRIGHT);
	}
}
