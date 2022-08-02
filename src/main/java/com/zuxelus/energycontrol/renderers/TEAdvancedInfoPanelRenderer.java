package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TEAdvancedInfoPanelRenderer extends TileEntitySpecialRenderer<TileEntityAdvancedInfoPanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/info_panel/panel_advanced_all.png");

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

	@Override
	public void render(TileEntityAdvancedInfoPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		CubeRenderer.rotateBlock(te.getFacing());

		int color = te.getColored() ? te.getColorBackground() : TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND;
		if (destroyStage > -1)
			bindTexture(DESTROY_STAGES[destroyStage]);
		else
			bindTexture(TEXTURE);

		int textureId = te.findTexture();
		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.rotateHor / 7;
		int rotateVert = te.rotateVert / 7;
		RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert);
		Screen screen = te.getScreen();
		if (screen != null) {
			if (thickness == 16 && rotateHor == 0 && rotateVert == 0) {
				if (destroyStage > -1)
					CubeRenderer.DESTROY.render(0.03125F);
				else {
					CubeRenderer.MODEL.render(0.03125F);
					bindTexture(TileEntityInfoPanelRenderer.SCREEN);
					TileEntityInfoPanelRenderer.drawFace(te.findTexture(), color, te.getPowered());
				}
			} else {
				if (destroyStage > -1)
					new CubeRenderer(0, 0, 0, 32, 32, 32, 32, 32, 0, 0, offset.addOffset(screen, te.getPos(), te.getFacing(), te.getRotation()), false).render(0.03125F);
				else {
					new CubeRenderer(0, 0, offset.addOffset(screen, te.getPos(), te.getFacing(), te.getRotation()), false).render(0.03125F);
					bindTexture(TileEntityInfoPanelRenderer.SCREEN);
					new CubeRenderer(textureId / 4 * 32, textureId % 4 * 32, offset.addOffset(screen, te.getPos(), te.getFacing(), te.getRotation()), true).render(0.03125F, color);
				}
			}

			CubeRenderer.rotateBlockText(te.getFacing());

			if (te.powered && destroyStage == -1) {
				List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
				if (joinedData != null)
					drawText(te, joinedData, thickness, offset);
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, byte thickness, RotationOffset offset) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getPos();
		float displayWidth = 1.0F;
		float displayHeight = 1.0F;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dx = pos.getX() - screen.maxX;
					dz = screen.minZ - pos.getZ();
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
					dx = pos.getX() - screen.maxX;
					dz = pos.getZ() - screen.maxZ;
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dx = screen.minX - pos.getX();
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
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

		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getFacing())
		{
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dx - 1.0F, dz, 0.0F);
			break;
		case SOUTH:
			GlStateManager.translate(dx, dz - 1.0F, 0.0F);
			break;
		case DOWN:
			break;
		case WEST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dz, dx, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dz - 1.0F, dx - 1.0F, 0.0F);
			break;
		}
		switch(panel.getRotation())
		{
		case DOWN:
			break;
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dx - 1.0F, dz, 0.0F);
			break;
		case SOUTH:
			GlStateManager.translate(dx, dz - 1.0F, 0.0F);
			break;
		case WEST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dz, dx, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(dz - 1.0F, dx - 1.0F, 0.0F);
			break;
		}

		double h = (offset.leftBottom - offset.rightBottom) / 32;
		double v = (offset.leftTop - offset.leftBottom) / 32;
		double b = Math.atan(h / displayWidth);
		double a = Math.atan(Math.cos(b) * v / displayHeight);
		int i = Integer.compare(0, offset.rotateVert);
		int j = Integer.compare(0, offset.rotateHor);
		GlStateManager.translate(displayWidth / 2, displayHeight / 2, 1 + (32 * h - offset.leftTop - offset.leftBottom) / 64);
		GlStateManager.rotate((float) Math.toDegrees(b), 0.0F, -1.0F, 0.0F);
		GlStateManager.rotate((float) Math.toDegrees(a), -1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F - (float) Math.toDegrees( // Law of cosines
			Math.acos((h * h + v * v) / 2 / Math.sqrt(displayWidth * displayWidth + h * h) / Math.sqrt(displayHeight * displayHeight + v * v))), 0.0F, 0.0F, i * j);
		GlStateManager.translate(0.0F, 0.001F * i, 0.001F);
		displayHeight = (float) ((displayHeight - 0.125F) / Math.cos(a));
		displayWidth = (float) ((displayWidth - 0.125F) / Math.cos(b));

		FontRenderer fontRenderer = getFontRenderer(); 
		// getMaxWidth
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(fontRenderer.getStringWidth(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = fontRenderer.FONT_HEIGHT + 2;
		int requiredHeight = lineHeight * joinedData.size();
		float scaleX = displayWidth / maxWidth;
		float scaleY = displayHeight / requiredHeight;
		float scale = Math.min(scaleX, scaleY);
		GlStateManager.scale(scale, -scale, scale);
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

		GlStateManager.disableAlpha();
		GlStateManager.disableLighting();

		int row = 0;
		int colorHex = TileEntityAdvancedInfoPanel.DEFAULT_TEXT;
		if (panel.getColored())
			colorHex = panel.getColorText();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
				fontRenderer.drawString(panelString.textLeft, offsetX - realWidth / 2,
						1 + offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorLeft != 0 ? panelString.colorLeft : colorHex);
			}
			if (panelString.textCenter != null) {
				fontRenderer.drawString(panelString.textCenter,
						-fontRenderer.getStringWidth(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorCenter != 0 ? panelString.colorCenter : colorHex);
			}
			if (panelString.textRight != null) {
				fontRenderer.drawString(panelString.textRight,
						realWidth / 2 - fontRenderer.getStringWidth(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorRight != 0 ? panelString.colorRight : colorHex);
			}
			row++;
		}

		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
