package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

public class TileEntityInfoPanelRenderer implements BlockEntityRenderer<TileEntityInfoPanel, BlockEntityFacingRenderState<TileEntityInfoPanel>> {
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
	public BlockEntityFacingRenderState<TileEntityInfoPanel> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityInfoPanel te, BlockEntityFacingRenderState<TileEntityInfoPanel> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
	}

	// the panel body is a baked model (PanelModel), only the text is drawn here
	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityInfoPanel> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityInfoPanel te = state.te;
		int combinedLight = state.lightCoords;
		if (!te.getPowered())
			return;
		matrixStack.pushPose();
		PanelCube.rotateBlock(matrixStack, te.getFacing(), te.getRotation());
		PanelCube.rotateBlockText(matrixStack, te.getFacing(), te.getRotation());

		List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
		drawText(te, joinedData, matrixStack, collector, combinedLight);
		matrixStack.popPose();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector collector, int combinedLight) {
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
			panel.renderImage(displayWidth, displayHeight, matrixStack, collector);
			matrixStack.rotate(Axis.YP.rotationDegrees(180));
		}
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, collector, font);
		}
	}

	public static void renderText(List<PanelString> joinedData, float displayWidth, float displayHeight, int colorHex, PoseStack matrixStack, SubmitNodeCollector collector, Font fontRenderer) {
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(fontRenderer.width(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = fontRenderer.lineHeight + 2;
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
			offsetY = 1;
		}

		int row = 0;
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				drawString(collector, matrixStack, panelString.textLeft, offsetX - realWidth / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorLeft != 0 ? panelString.colorLeft : colorHex);
			if (panelString.textCenter != null)
				drawString(collector, matrixStack, panelString.textCenter, -fontRenderer.width(panelString.textCenter) / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorCenter != 0 ? panelString.colorCenter : colorHex);
			if (panelString.textRight != null)
				drawString(collector, matrixStack, panelString.textRight, realWidth / 2 - fontRenderer.width(panelString.textRight),
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorRight != 0 ? panelString.colorRight : colorHex);
			row++;
		}
	}

	// full bright; the polygon offset keeps the text off the screen face
	private static void drawString(SubmitNodeCollector collector, PoseStack matrixStack, String text, float x, float y, int color) {
		RenderHelper.drawString(matrixStack, collector, text, x, y, color, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT);
	}

	@Override
	public boolean shouldRenderOffScreen() { // fabric only
		return true;
	}

	@Override
	public int getViewDistance() { // fabric only
		return 65536;
	}
}
