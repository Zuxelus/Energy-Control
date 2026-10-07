package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class TileEntityHoloPanelRenderer implements BlockEntityRenderer<TileEntityHoloPanel, BlockEntityFacingRenderState<TileEntityHoloPanel>> {
	private final Font font;

	public TileEntityHoloPanelRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public BlockEntityFacingRenderState<TileEntityHoloPanel> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityHoloPanel te, BlockEntityFacingRenderState<TileEntityHoloPanel> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
	}

	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityHoloPanel> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityHoloPanel te = state.te;
		int combinedLight = state.lightCoords;
		matrixStack.pushPose();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.5F, 0.0F);
			break;
		case SOUTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, -0.5F, -1.0F);
			break;
		case DOWN:
			matrixStack.rotate(Axis.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.5F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, -0.5F, 0.0F);
			break;
		}

		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, joinedData, matrixStack, collector, combinedLight);
		}
		matrixStack.popPose();
	}

	@SuppressWarnings("incomplete-switch")
	private void drawText(TileEntityHoloPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector collector, int combinedLight) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		int power = panel.getPower();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = power - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case NORTH:
				dz = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				dz = dz - power + 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				dz = dz + power - 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case WEST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = dy - power + 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = dy + power - 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			}
		}

		matrixStack.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		matrixStack.rotate(Axis.XP.rotationDegrees(-90));
		switch(panel.getFacing())
		{
		case NORTH:
			matrixStack.rotate(Axis.ZP.rotationDegrees(180));
			break;
		case SOUTH:
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90));
			break;
		}
		float imageWidth = 0.475F + (displayWidth - 0.875F) / 2F;
		float imageHeight = 0.5F + (power - 1) / 2F;
		// debugQuads is not culled and doesn't write depth, so one quad is visible from both sides and doesn't hide water behind it
		IHasBars.drawTransparentRect(matrixStack, collector, imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F * (power + 1) / 2);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorTextHex();
			TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, collector, font);
		}
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
