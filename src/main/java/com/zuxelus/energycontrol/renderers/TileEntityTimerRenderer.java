package com.zuxelus.energycontrol.renderers;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public class TileEntityTimerRenderer implements BlockEntityRenderer<TileEntityTimer, BlockEntityFacingRenderState<TileEntityTimer>> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/block/timer/all.png");
	private static final Identifier TEXTURE_ACTIVE = Identifier.parse(EnergyControl.MODID + ":textures/block/timer/active.png");
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);
	private final Font font;

	public TileEntityTimerRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public BlockEntityFacingRenderState<TileEntityTimer> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityTimer te, BlockEntityFacingRenderState<TileEntityTimer> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
		state.light = CubeRenderer.getBlockLight(te);
	}

	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityTimer> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityTimer te = state.te;
		int combinedLight = state.lightCoords;
		matrixStack.pushPose();
		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		model.render(matrixStack, collector, RenderTypes.entitySolid(te.getIsWorking() ? TEXTURE_ACTIVE : TEXTURE), state.light);
		String time = te.getTimeString();
		matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.575F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		RenderHelper.drawString(matrixStack, collector, time, -font.width(time) / 2, -font.lineHeight, 0x000000, Font.DisplayMode.NORMAL, combinedLight);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
