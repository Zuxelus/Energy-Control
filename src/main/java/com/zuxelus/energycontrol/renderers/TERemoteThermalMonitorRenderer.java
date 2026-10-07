package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class TERemoteThermalMonitorRenderer extends TileRenderer<TileEntityRemoteThermalMonitor> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/remote_thermal_monitor/all.png");
	private final Font font;

	public TERemoteThermalMonitorRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	protected void render(TileEntityRemoteThermalMonitor te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		PanelCube.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		PanelCube.MODEL.render(matrixStack, buffer, RenderTypes.entitySolid(TEXTURE), PanelCube.getBlockLight(te), combinedOverlay);
		matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
		matrixStack.rotate(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(0.0F, -0.5F, 0.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = status > -1 ? Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16 : 1;
			RenderHelper.texturedRect(matrixStack, buffer, TEXTURE, rate, 0, 1, 0.75F, 0, rate * 0.25F, 0, 0.25F, 0.1875F, 0xFFFFFFFF);
		}

		matrixStack.rotate(Axis.XP.rotationDegrees(180.0F));
		matrixStack.rotate(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		RenderHelper.drawString(matrixStack, buffer, text, -font.width(text) / 2, -font.lineHeight, 0x000000, LightCoordsUtil.FULL_BRIGHT);
		matrixStack.popPose();
	}
}
