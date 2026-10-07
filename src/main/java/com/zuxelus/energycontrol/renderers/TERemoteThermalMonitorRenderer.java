package com.zuxelus.energycontrol.renderers;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

// The body is a block model (remote_thermo.json); only the heat bar and the heat level text are drawn here
public class TERemoteThermalMonitorRenderer implements BlockEntityRenderer<TileEntityRemoteThermalMonitor, BlockEntityFacingRenderState<TileEntityRemoteThermalMonitor>> {
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/remote_thermal_monitor/all.png");
	// the bar image is the top left 32x11 corner of all.png (128x128), shown at 32 pixels per block
	private static final float BAR_HEIGHT = 11 / 32.0F;
	private static final float BAR_U = 0.25F;
	private static final float BAR_V = 11 / 128.0F;
	private static final int FULL_BRIGHT = LightCoordsUtil.FULL_BRIGHT;
	private final Font font;

	public TERemoteThermalMonitorRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public BlockEntityFacingRenderState<TileEntityRemoteThermalMonitor> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityRemoteThermalMonitor te, BlockEntityFacingRenderState<TileEntityRemoteThermalMonitor> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
	}

	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityRemoteThermalMonitor> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityRemoteThermalMonitor te = state.te;
		matrixStack.pushPose();
		// turn the north facing layout towards the front of the block
		Direction facing = te.getFacing() == null ? Direction.NORTH : te.getFacing();
		matrixStack.translate(0.5F, 0.0F, 0.5F);
		matrixStack.rotate(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
		matrixStack.translate(-0.5F, 0.0F, -0.5F);

		matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
		matrixStack.rotate(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(0.0F, -0.5F, 0.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = status > -1 ? Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16 : 1;
			RenderHelper.texturedRect(matrixStack, collector, TEXTURE, rate, 0, 1, BAR_HEIGHT, 0, rate * BAR_U, 0, BAR_U, BAR_V, 0xFFFFFFFF);
		}

		matrixStack.rotate(Axis.XP.rotationDegrees(180.0F));
		matrixStack.rotate(Axis.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		RenderHelper.drawString(matrixStack, collector, text, -font.width(text) / 2, -font.lineHeight, 0x000000, Font.DisplayMode.NORMAL, FULL_BRIGHT);
		matrixStack.popPose();
	}
}
