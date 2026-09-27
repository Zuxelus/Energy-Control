package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.renderers.ModRenderTypes;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {

	boolean enableBars(ItemStack stack);

	void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack, SubmitNodeCollector collector);

	static void drawTransparentRect(PoseStack matrixStack, SubmitNodeCollector collector, float left, float top, float right, float bottom, float zLevel, int color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		collector.submitCustomGeometry(matrixStack, ModRenderTypes.screenColor(), (pose, builder) -> {
			builder.addVertex(pose, right, top, zLevel).setColor(f1, f2, f3, f).setUv(1, 0).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, left, top, zLevel).setColor(f1, f2, f3, f).setUv(0, 0).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, left, bottom, zLevel).setColor(f1, f2, f3, f).setUv(0, 1).setLight(LightCoordsUtil.FULL_BRIGHT);
			builder.addVertex(pose, right, bottom, zLevel).setColor(f1, f2, f3, f).setUv(1, 1).setLight(LightCoordsUtil.FULL_BRIGHT);
		});
	}
}
