package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityInfoPanelRenderer extends TileEntitySpecialRenderer<TileEntityInfoPanel> {
	private static final ResourceLocation[] TEXTUREOFF;
	private static final ResourceLocation[] TEXTUREON;
	private static final CubeRenderer[] model;
	public static final CubeRenderer DESTROY = new CubeRenderer(0, 0, 0, 32, 32, 32, 32, 32, 0, 0);

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/off/all%d.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/on/all%d.png", i));
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

	@Override
	public void render(TileEntityInfoPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 2;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}

		if (destroyStage > -1)
			bindTexture(DESTROY_STAGES[destroyStage]);
		else if (te.getPowered())
			bindTexture(TEXTUREON[color]);
		else
			bindTexture(TEXTUREOFF[color]);

		if (destroyStage > -1)
			DESTROY.render(0.03125F);
		else {
			model[te.findTexture()].render(0.03125F);

			if (te.getPowered()) {
				List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
				drawText(te, joinedData);
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getPos();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
				case SOUTH:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
				case WEST:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				}
				break;
			case DOWN:
				switch (panel.getRotation()) {
				case NORTH:
				case SOUTH:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
				case WEST:
					dz = - (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				}
				break;
			case NORTH:
				dz = pos.getY() - screen.maxY - screen.minY + pos.getY();
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case WEST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = pos.getY() - screen.maxY - screen.minY + pos.getY();
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		GlStateManager.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getFacing())
		{
		case DOWN:
			break;
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			break;
		case SOUTH:
			break;
		case WEST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
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
			break;
		case SOUTH:
			break;
		case WEST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			break;
		}
		if (panel.isTouchCard() || panel.hasBars()) {
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.disableLighting();
			panel.renderImage(rendererDispatcher.renderEngine, displayWidth, displayHeight);
			GlStateManager.enableLighting();
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		}
		if (joinedData != null) {
			GlStateManager.translate(0, 0, 0.0002F);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			renderText(joinedData, displayWidth, displayHeight, colorHex, getFontRenderer());
		}
	}

	public static void renderText(List<PanelString> joinedData, float displayWidth, float displayHeight, int colorHex, FontRenderer fontRenderer) {
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
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				fontRenderer.drawString(panelString.textLeft, offsetX - realWidth / 2,
						offsetY - realHeight / 2 + row * lineHeight, panelString.colorLeft != 0 ? panelString.colorLeft : colorHex);
			if (panelString.textCenter != null)
				fontRenderer.drawString(panelString.textCenter, -fontRenderer.getStringWidth(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight, panelString.colorCenter != 0 ? panelString.colorCenter : colorHex);
			if (panelString.textRight != null)
				fontRenderer.drawString(panelString.textRight, realWidth / 2 - fontRenderer.getStringWidth(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight, panelString.colorRight != 0 ? panelString.colorRight : colorHex);
			row++;
		}

		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
