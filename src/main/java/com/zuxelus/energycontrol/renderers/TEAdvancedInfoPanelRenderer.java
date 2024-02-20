package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class TEAdvancedInfoPanelRenderer implements BlockEntityRenderer<TileEntityAdvancedInfoPanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/block/info_panel/panel_advanced_all.png");
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
		font = ctx.getFont();
	}

	@Override
	public void render(TileEntityAdvancedInfoPanel te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = TileEntityInfoPanelRenderer.getBlockLight(te);
		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND;
		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));

		int textureId = te.findTexture();
		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.rotateHor / 7;
		int rotateVert = te.rotateVert / 7;
		RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert);
		Screen screen = te.getScreen();
		if (screen != null) {
			RotationOffset offsetScreen = offset.addOffset(screen, te.getBlockPos(), te.getFacing(), te.getRotation());
			if (thickness == 16 && rotateHor == 0 && rotateVert == 0) {
				CubeRenderer.MODEL.render(matrixStack, vertexBuilder, light, combinedOverlay);
				VertexConsumer vertexScreen = buffer.getBuffer(RenderType.entitySolid(TileEntityInfoPanelRenderer.SCREEN));
				TileEntityInfoPanelRenderer.drawFace(matrixStack.last(), vertexScreen, te.findTexture(), color, te.getPowered(), combinedLight, combinedOverlay);
			} else {
				CubeRenderer.getModel(offsetScreen).render(matrixStack, vertexBuilder, light, combinedOverlay);
				VertexConsumer vertexScreen = buffer.getBuffer(RenderType.entitySolid(TileEntityInfoPanelRenderer.SCREEN));
				CubeRenderer.getFaceModel(offsetScreen, textureId).render(matrixStack, vertexBuilder, light, combinedOverlay, color);
			}

			CubeRenderer.rotateBlockText(matrixStack, te.getFacing(), te.getRotation());

			if (te.powered) {
				List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
				if (joinedData != null)
					drawText(te, joinedData, matrixStack, buffer, combinedLight, thickness, offset);
			}
		}
		matrixStack.popPose();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, byte thickness, RotationOffset offset) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		float displayWidth = 1.0F;
		float displayHeight = 1.0F;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = screen.minZ - pos.getZ();
					dy = screen.maxX - pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = screen.maxZ - pos.getZ();
					dy = screen.minX - pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = screen.maxZ - pos.getZ();
					dy = screen.maxX - pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = screen.minZ - pos.getZ();
					dy = screen.minX - pos.getX();
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
				dz = pos.getY() - screen.minY;
				dy = pos.getX() - screen.maxX;
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = pos.getY() - screen.minY;
				dy = screen.minX - pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dy = screen.minZ - pos.getZ();
				dz = pos.getY() - screen.minY;
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dy = pos.getZ() - screen.maxZ;
				dz = pos.getY() - screen.minY;
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.translate(dy, dx, dz);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
		switch (panel.getFacing()) {
		case UP:
			switch(panel.getRotation()) {
			case UP:
				break;
			case NORTH:
				matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.translate(0.0F, 0.0F, 0.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			}
		}

		double h = (offset.leftBottom - offset.rightBottom) / 32;
		double v = (offset.leftTop - offset.leftBottom) / 32;
		double b = Math.atan(h / displayWidth);
		double a = Math.atan(Math.cos(b) * v / displayHeight);
		int i = offset.rotateVert == 0 ? 0 : offset.rotateVert > 0 ? -1 : 1;
		int j = offset.rotateHor == 0 ? 0 : offset.rotateHor > 0 ? -1 : 1;
		matrixStack.translate(displayWidth / 2, displayHeight / 2, 1 + (32 * h - offset.leftTop - offset.leftBottom) / 64);
		matrixStack.mulPose(Axis.YN.rotationDegrees((float) Math.toDegrees(b)));
		matrixStack.mulPose(Axis.XN.rotationDegrees((float) Math.toDegrees(a)));
		/*matrixStack.mulPose(new Vector3f(0.0F, 0.0F, i * j).rotationDegrees(90.0F - (float) Math.toDegrees( // Law of cosines
			Math.acos((h * h + v * v) / 2 / Math.sqrt(displayWidth * displayWidth + h * h) / Math.sqrt(displayHeight * displayHeight + v * v)))));*/
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

		int lineHeight = font.lineHeight + 3;
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
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorText();
		for (PanelString panelString : joinedData) {
			/*if (panelString.textLeft != null) {
				font.drawInBatch(panelString.textLeft, offsetX - realWidth / 2,
						1 + offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorLeft != 0 ? panelString.colorLeft : colorHex, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
			}
			if (panelString.textCenter != null) {
				font.drawInBatch(panelString.textCenter,
						-font.width(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorCenter != 0 ? panelString.colorCenter : colorHex, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
			}
			if (panelString.textRight != null) {
				font.drawInBatch(panelString.textRight,
						realWidth / 2 - font.width(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorRight != 0 ? panelString.colorRight : colorHex, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
			}*/
			row++;
		}
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
