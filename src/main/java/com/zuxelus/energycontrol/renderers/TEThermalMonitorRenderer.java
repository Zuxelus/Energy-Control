package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class TEThermalMonitorRenderer extends TileRenderer<TileEntityThermalMonitor> {
	private static final Identifier TEXTURE0 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all0.png");
	private static final Identifier TEXTURE1 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all1.png");
	private static final Identifier TEXTURE2 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all2.png");
	private final Font font;

	public TEThermalMonitorRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	protected void render(TileEntityThermalMonitor te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeSmallRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		Identifier texture;
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
		CubeSmallRenderer.MODEL.render(matrixStack, buffer, RenderTypes.entitySolid(texture), CubeSmallRenderer.getBlockLight(te), combinedOverlay);

		matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
		matrixStack.rotate(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.55F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String value = String.valueOf(te.getHeatLevel());
		drawString(buffer, matrixStack, value, -font.width(value) / 2, -font.lineHeight, 0x000000, combinedLight);
		matrixStack.popPose();
	}
}
