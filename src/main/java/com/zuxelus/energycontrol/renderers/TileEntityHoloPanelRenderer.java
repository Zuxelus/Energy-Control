package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class TileEntityHoloPanelRenderer extends TileRenderer<TileEntityHoloPanel> {
	private final Font font;

	public TileEntityHoloPanelRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public AABB getRenderBoundingBox(TileEntityHoloPanel te) {
		return te.getRenderBoundingBox();
	}

	@Override
	protected void render(TileEntityHoloPanel te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
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
			drawText(te, joinedData, matrixStack, buffer, combinedLight);
		}
		matrixStack.popPose();
	}

	private void drawText(TileEntityHoloPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight) {
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
			default:
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
		default:
			break;
		}
		float imageWidth = 0.475F + (displayWidth - 0.875F) / 2F;
		float imageHeight = 0.5F + (power - 1) / 2F;
		// the render type is back-face culled, so draw both windings to be visible from both sides
		IHasBars.drawTransparentRect(matrixStack, buffer, ModRenderTypes.holoColor(), imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
		IHasBars.drawTransparentRect(matrixStack, buffer, ModRenderTypes.holoColor(), -imageWidth, imageHeight, imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F * (power + 1) / 2);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, buffer, font);
		}
	}
}
