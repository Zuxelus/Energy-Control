package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
	private static final ResourceLocation[] TEXTUREOFF;
	private static final ResourceLocation[] TEXTUREON;
	private static final CubeRenderer[] model;
	private final Font font;

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/off/alladv%d.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/on/alladv%d.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

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
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 6;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 6;
		}
		VertexConsumer vertexBuilder;
		if (te.getPowered())
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREON[color]));
		else
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREOFF[color]));

		int textureId = te.findTexture();
		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.rotateHor / 7;
		int rotateVert = te.rotateVert / 7;
		RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert);
		Screen screen = te.getScreen();
		if (screen != null) {
			if (thickness == 16 && rotateHor == 0 && rotateVert == 0)
				model[textureId].render(matrixStack, vertexBuilder, light, combinedOverlay);
			else
				new CubeRenderer(textureId / 4 * 32 + 64, textureId % 4 * 32 + 64, offset.addOffset(screen, te.getBlockPos(), te.getFacing(), te.getRotation())).render(matrixStack, vertexBuilder, light, combinedOverlay);
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

		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
			matrixStack.translate(dx - 1.0F, dz, 0.0F);
			break;
		case SOUTH:
			matrixStack.translate(dx, dz - 1.0F, 0.0F);
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
			matrixStack.translate(dz, dx, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
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
		matrixStack.mulPose(Vector3f.YN.rotationDegrees((float) Math.toDegrees(b)));
		matrixStack.mulPose(Vector3f.XN.rotationDegrees((float) Math.toDegrees(a)));
		matrixStack.mulPose(new Vector3f(0.0F, 0.0F, i * j).rotationDegrees(90.0F - (float) Math.toDegrees( // Law of cosines
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
			colorHex = panel.getColorText();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
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
			}
			row++;
		}

		//matrixStack.enableLighting();
		//matrixStack.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
