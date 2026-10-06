package com.zuxelus.energycontrol.renderers;

import java.util.List;
import net.minecraft.util.LightCoordsUtil;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

public class TEAdvancedInfoPanelRenderer implements BlockEntityRenderer<TileEntityAdvancedInfoPanel, BlockEntityFacingRenderState<TileEntityAdvancedInfoPanel>> {
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

	public TEAdvancedInfoPanelRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public BlockEntityFacingRenderState<TileEntityAdvancedInfoPanel> createRenderState() {
		return new BlockEntityFacingRenderState<>();
	}

	@Override
	public void extractRenderState(TileEntityAdvancedInfoPanel te, BlockEntityFacingRenderState<TileEntityAdvancedInfoPanel> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(te, state, partialTicks, cameraPosition, breakProgress);
		state.te = te;
	}

	// the panel body is a baked model (PanelModel), only the text is drawn here
	@Override
	public void submit(BlockEntityFacingRenderState<TileEntityAdvancedInfoPanel> state, PoseStack matrixStack, SubmitNodeCollector collector, CameraRenderState camera) {
		TileEntityAdvancedInfoPanel te = state.te;
		int combinedLight = state.lightCoords;
		Screen screen = te.getScreen();
		if (!te.powered || screen == null)
			return;
		matrixStack.pushPose();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.rotate(Axis.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		RotationOffset offset = new RotationOffset(thickness * 2, te.rotateHor / 7, te.rotateVert / 7);
		List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
		if (joinedData != null)
			drawText(te, joinedData, matrixStack, collector, combinedLight, thickness, offset);
		matrixStack.popPose();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector collector, int combinedLight, byte thickness, RotationOffset offset) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		float displayWidth = 1.0F;
		float displayHeight = 1.0F;
		float dx = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dx = screen.minX - pos.getX();
					dz = pos.getZ() - screen.maxZ;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dx = screen.minX - pos.getX();
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case DOWN:
					break;
				case UP:
					break;
				}
				break;
			case DOWN:
				switch (panel.getRotation()) {
				case NORTH:
					dx = pos.getX() - screen.maxX;
					dz = pos.getZ() - screen.maxZ;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dx = screen.minX - pos.getX();
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dx = pos.getX() - screen.maxX;
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dx = screen.minX - pos.getX();
					dz = pos.getZ() - screen.maxZ;
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
				dx = pos.getX() - screen.maxX;
				dz = screen.minY - pos.getY();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dx = screen.minX - pos.getX();
				dz = screen.minY - pos.getY();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case WEST:
				dz = screen.minZ - pos.getZ();
				dx = screen.minY - pos.getY();
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ;
				dx = screen.minY - pos.getY();
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.rotate(Axis.XP.rotationDegrees(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Axis.ZP.rotationDegrees(180));
			matrixStack.translate(dx - 1.0F, dz, 0.0F);
			break;
		case SOUTH:
			matrixStack.translate(dx, dz - 1.0F, 0.0F);
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
			matrixStack.translate(dz, dx, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90));
			matrixStack.translate(dz - 1.0F, dx - 1.0F, 0.0F);
			break;
		}

		double h = (offset.leftBottom - offset.rightBottom) / 32;
		double v = (offset.leftTop - offset.leftBottom) / 32;
		double b = Math.atan(h / displayWidth);
		double a = Math.atan(Math.cos(b) * v / displayHeight);
		int i = offset.rotateVert == 0 ? 0 : offset.rotateVert > 0 ? -1 : 1;
		int j = offset.rotateHor == 0 ? 0 : offset.rotateHor > 0 ? -1 : 1;
		matrixStack.translate(displayWidth / 2, displayHeight / 2, 1 + (32 * h - offset.leftTop - offset.leftBottom) / 64);
		matrixStack.rotate(Axis.YN.rotationDegrees((float) Math.toDegrees(b)));
		matrixStack.rotate(Axis.XN.rotationDegrees((float) Math.toDegrees(a)));
		if (i * j != 0)
			matrixStack.rotate((i * j > 0 ? Axis.ZP : Axis.ZN).rotationDegrees(90.0F - (float) Math.toDegrees( // Law of cosines
			Math.acos((h * h + v * v) / 2 / Math.sqrt(displayWidth * displayWidth + h * h) / Math.sqrt(displayHeight * displayHeight + v * v)))));
		matrixStack.translate(0.0F, 0.001F * i, 0.001F);
		displayHeight = (float) ((displayHeight - 0.125F) / Math.cos(a));
		displayWidth = (float) ((displayWidth - 0.125F) / Math.cos(b));

		// getMaxWidth
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(font.width(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = font.lineHeight + 2;
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
			offsetY = 0;
		}

		//matrixStack.disableLighting();

		int row = 0;
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorTextHex();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
				RenderHelper.drawString(matrixStack, collector, panelString.textLeft, offsetX - realWidth / 2,
						1 + offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorLeft != 0 ? panelString.colorLeft : colorHex, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT);
			}
			if (panelString.textCenter != null) {
				RenderHelper.drawString(matrixStack, collector, panelString.textCenter,
						-font.width(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorCenter != 0 ? panelString.colorCenter : colorHex, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT);
			}
			if (panelString.textRight != null) {
				RenderHelper.drawString(matrixStack, collector, panelString.textRight,
						realWidth / 2 - font.width(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorRight != 0 ? panelString.colorRight : colorHex, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT);
			}
			row++;
		}

		//matrixStack.enableLighting();
		//matrixStack.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	// a screen can be larger than the core block: keep drawing the text while only other parts of it are in view
	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
