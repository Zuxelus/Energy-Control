package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class TEThermalMonitorRenderer implements BlockEntityRenderer<TileEntityThermalMonitor, BlockEntityFacingRenderState<TileEntityThermalMonitor>> {
	private static final Identifier TEXTURE0 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all0.png");
	private static final Identifier TEXTURE1 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all1.png");
	private static final Identifier TEXTURE2 = Identifier.fromNamespaceAndPath(EnergyControl.MODID, "textures/block/thermal_monitor/all2.png");
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);
	private final Font font;

	public TEThermalMonitorRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public BlockEntityFacingRenderState<TileEntityThermalMonitor> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityThermalMonitor te, BlockEntityFacingRenderState<TileEntityThermalMonitor> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
		state.light = TileEntityInfoPanelRenderer.getBlockLight(te);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityThermalMonitor> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityThermalMonitor te = state.te;
		int combinedLight = state.lightCoords;
		matrixStack.pushPose();
		switch (te.getFacing()) {
		case UP:
			switch (te.getRotation()) {
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.rotate(Axis.XP.rotationDegrees(180.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}

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
		model.render(matrixStack, collector, RenderTypes.entitySolid(texture), state.light);
		int value = te.getHeatLevel();
		matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.45F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		RenderHelper.drawString(matrixStack, collector, String.valueOf(value), -font.width(String.valueOf(value)) / 2, -font.lineHeight, 0x000000, Font.DisplayMode.NORMAL, combinedLight);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
