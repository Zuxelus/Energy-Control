package com.zuxelus.energycontrol.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.renderers.RenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;

/**
 * Used to draw (progress) bars on Info Panels
 */
public interface IHasBars {

	boolean enableBars(ItemStack stack);

	void renderBars(float displayWidth, float displayHeight, ICardReader reader, PoseStack matrixStack, SubmitNodeCollector collector);

	static void drawTransparentRect(PoseStack matrixStack, SubmitNodeCollector collector, float left, float top, float right, float bottom, float zLevel, int color) {
		RenderHelper.fillRect(matrixStack, collector, left, top, right, bottom, zLevel, color);
	}
}
