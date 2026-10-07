package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.renderers.CubeRenderer.PositionTextureVertex;
import com.zuxelus.energycontrol.renderers.CubeRenderer.TexturedQuad;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class TileEntityInfoPanelRenderer extends TileRenderer<TileEntityInfoPanel> {
	private final Font font;

	private static String implodeArray(String[] inputArray, String glueString) {
		String output = "";
		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : inputArray) {
				if (s == null || s.isEmpty())
					continue;
				sb.append(glueString);
				sb.append(s);
			}
			output = sb.toString();
			if (output.length() > 1)
				output = output.substring(1);
		}
		return output;
	}

	public TileEntityInfoPanelRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public AABB getRenderBoundingBox(TileEntityInfoPanel te) {
		return te.getRenderBoundingBox();
	}

	@Override
	protected void render(TileEntityInfoPanel te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		// the panel body is a baked model (PanelModel), only the text is drawn here
		matrixStack.pushPose();
		PanelCube.rotateBlock(matrixStack, te.getFacing(), te.getRotation());
		PanelCube.rotateBlockText(matrixStack, te.getFacing(), te.getRotation());
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, joinedData, matrixStack, buffer, combinedLight);
		}
		matrixStack.popPose();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case DOWN:
					break;
				case UP:
					break;
				}
				break;
			case NORTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = - (pos.getX() - screen.maxX - screen.minX + pos.getX());
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dy = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dy = - (pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ);
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.translate(0.5F - dy / 2, 1.01F - dx / 2 , -0.5F - dz / 2);
		matrixStack.rotate(Axis.XP.rotationDegrees(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Axis.ZP.rotationDegrees(180));
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90));
			break;
		}

		if (panel.isTouchCard() || panel.hasBars()) {
			matrixStack.rotate(Axis.YP.rotationDegrees(180));
			panel.renderImage(displayWidth, displayHeight, matrixStack, buffer);
			matrixStack.rotate(Axis.YP.rotationDegrees(180));
		}
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, buffer, font);
		}
	}

	public static void renderText(List<PanelString> joinedData, float displayWidth, float displayHeight, int colorHex, PoseStack matrixStack, SubmitNodeCollector buffer, Font fontRenderer) {
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(fontRenderer.width(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = fontRenderer.lineHeight + 3;
		int requiredHeight = lineHeight * joinedData.size();
		float scaleX = displayWidth / maxWidth;
		float scaleY = displayHeight / requiredHeight;
		float scale = Math.min(scaleX, scaleY);
		matrixStack.scale(scale, -scale, scale);
		int realHeight = (int) Math.floor(displayHeight / scale);
		int realWidth = (int) Math.floor(displayWidth / scale);
		int offsetX;
		int offsetY;
		if (scaleX < scaleY) {
			offsetX = 2;
			offsetY = (realHeight - requiredHeight) / 2;
		} else {
			offsetX = (realWidth - maxWidth) / 2 + 2;
			offsetY = 3;
		}

		int row = 0;
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				RenderHelper.drawString(matrixStack, buffer, panelString.textLeft, offsetX - realWidth / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorLeft != 0 ? panelString.colorLeft : colorHex, LightCoordsUtil.FULL_BRIGHT);
			if (panelString.textCenter != null)
				RenderHelper.drawString(matrixStack, buffer, panelString.textCenter, -fontRenderer.width(panelString.textCenter) / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorCenter != 0 ? panelString.colorCenter : colorHex, LightCoordsUtil.FULL_BRIGHT);
			if (panelString.textRight != null)
				RenderHelper.drawString(matrixStack, buffer, panelString.textRight, realWidth / 2 - fontRenderer.width(panelString.textRight),
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorRight != 0 ? panelString.colorRight : colorHex, LightCoordsUtil.FULL_BRIGHT);
			row++;
		}
	}

}
