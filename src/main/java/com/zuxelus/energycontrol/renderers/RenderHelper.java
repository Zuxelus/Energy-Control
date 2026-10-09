package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.StringDecomposer;

/**
 * Since 26.1 block entities are no longer drawn immediately: geometry and text are submitted
 * to a {@link SubmitNodeCollector} and rendered later. Every submit copies the current pose.
 */
public final class RenderHelper {

	/** a (translucent) coloured quad, not affected by light */
	public static void fillRect(PoseStack matrixStack, SubmitNodeCollector collector, float left, float top, float right, float bottom, float z, int color) {
		fillRect(matrixStack, collector, ModRenderTypes.screenColor(), left, top, right, bottom, z, color);
	}

	public static void fillRect(PoseStack matrixStack, SubmitNodeCollector collector, RenderType renderType, float left, float top, float right, float bottom, float z, int color) {
		collector.submitCustomGeometry(matrixStack, renderType, (pose, buffer) -> {
			buffer.addVertex(pose, right, top, z).setColor(color).setUv(1, 0).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, left, top, z).setColor(color).setUv(0, 0).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, left, bottom, z).setColor(color).setUv(0, 1).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, right, bottom, z).setColor(color).setUv(1, 1).setLight(LightCoordsUtil.FULL_BRIGHT);
		});
	}

	/** a textured quad drawn full bright; (u0, v0) belongs to (x0, y0) */
	public static void texturedRect(PoseStack matrixStack, SubmitNodeCollector collector, Identifier texture,
			float x0, float y0, float x1, float y1, float z, float u0, float v0, float u1, float v1, int color) {
		collector.submitCustomGeometry(matrixStack, ModRenderTypes.screenImage(texture), (pose, buffer) -> {
			buffer.addVertex(pose, x0, y1, z).setColor(color).setUv(u0, v1).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, x1, y1, z).setColor(color).setUv(u1, v1).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, x1, y0, z).setColor(color).setUv(u1, v0).setLight(LightCoordsUtil.FULL_BRIGHT);
			buffer.addVertex(pose, x0, y0, z).setColor(color).setUv(u0, v0).setLight(LightCoordsUtil.FULL_BRIGHT);
		});
	}

	/** text colours are ARGB now: an RGB colour without alpha would be invisible */
	public static void drawString(PoseStack matrixStack, SubmitNodeCollector collector, String text, float x, float y, int color, Font.DisplayMode mode, int light) {
		if (text == null)
			return;
		collector.submitText(matrixStack, x, y, toSequence(text), false, mode, light, ARGB.opaque(color), 0, 0);
	}

	private static FormattedCharSequence toSequence(String text) {
		return sink -> StringDecomposer.iterateFormatted(text, Style.EMPTY, sink);
	}

	private RenderHelper() {}
}
