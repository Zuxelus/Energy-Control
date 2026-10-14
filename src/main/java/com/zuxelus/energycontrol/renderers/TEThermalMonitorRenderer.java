package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class TEThermalMonitorRenderer extends TileRenderer<TileEntityThermalMonitor> {
	private static final ResourceLocation TEXTURE0 = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all0.png");
	private static final ResourceLocation TEXTURE1 = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all1.png");
	private static final ResourceLocation TEXTURE2 = ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all2.png");
	private final Font font;

	public TEThermalMonitorRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@Override
	protected void render(TileEntityThermalMonitor te, PoseStack matrixStack, MultiBufferSource collector, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		ResourceLocation texture;
		switch (te.getStatus()) {
		case 0:
			texture = TEXTURE1;
			break;
		case 1:
			texture = TEXTURE2;
			break;
		default:
			texture = TEXTURE0;
			break;
		}
		CubeRenderer.MODEL.render(matrixStack, collector, RenderType.entitySolid(texture), CubeRenderer.getBlockLight(te), combinedOverlay);

		matrixStack.mulPose(Axis.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.45F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String value = String.valueOf(te.getHeatLevel());
		RenderHelper.drawString(matrixStack, collector, value, -font.width(value) / 2, -font.lineHeight, 0x000000, Font.DisplayMode.NORMAL, combinedLight);
		matrixStack.popPose();
	}
}
