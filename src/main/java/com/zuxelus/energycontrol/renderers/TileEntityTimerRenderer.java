package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class TileEntityTimerRenderer extends TileRenderer<TileEntityTimer> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/block/timer/all.png");
	private static final Identifier TEXTURE_ACTIVE = Identifier.parse(EnergyControl.MODID + ":textures/block/timer/active.png");
	private final Font font;

	public TileEntityTimerRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	protected void render(TileEntityTimer te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		CubeRenderer.MODEL.render(matrixStack, buffer, RenderTypes.entitySolid(te.getIsWorking() ? TEXTURE_ACTIVE : TEXTURE), CubeRenderer.getBlockLight(te), combinedOverlay);
		String time = te.getTimeString();
		matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.575F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		RenderHelper.drawString(matrixStack, buffer, time, -font.width(time) / 2, -font.lineHeight, 0x000000, Font.DisplayMode.NORMAL, combinedLight);
		matrixStack.popPose();
	}
}
