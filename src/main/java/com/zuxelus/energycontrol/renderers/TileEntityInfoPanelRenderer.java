package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityInfoPanelRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation TEXTUREOFF[];
	private static final ResourceLocation TEXTUREON[];
	private static final CubeRenderer model[];

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
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i] == null || inputArray[i].isEmpty())
					continue;
				sb.append(glueString);
				sb.append(inputArray[i]);
			}
			output = sb.toString();
			if (output.length() > 1)
				output = output.substring(1);
		}
		return output;
	}

	public void renderTileEntityAt(TileEntityInfoPanel te, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		switch (te.getFacingForge()) {
		case UP:
			break;
		case NORTH:
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 2;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}
		if (te.getPowered())
			bindTexture(TEXTUREON[color]);
		else
			bindTexture(TEXTUREOFF[color]);

		model[te.findTexture()].render(0.03125F);
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			if (joinedData != null)
				drawText(te, joinedData);
		}
		GL11.glPopMatrix();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData) {
		Screen screen = panel.getScreen();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacingForge()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = (panel.zCoord - screen.maxZ - screen.minZ + panel.zCoord);
					dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = (panel.zCoord - screen.maxZ - screen.minZ + panel.zCoord);
					dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = (panel.zCoord - screen.maxZ - screen.minZ + panel.zCoord);
					dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = (panel.zCoord - screen.maxZ - screen.minZ + panel.zCoord);
					dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
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
				dz = (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = - (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dz = panel.zCoord - screen.maxZ + panel.zCoord - screen.minZ;
				dy = (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = panel.zCoord - screen.maxZ + panel.zCoord - screen.minZ;
				dy = - (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		GL11.glTranslatef(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			break;
		case EAST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			break;
		}

		if (panel.isTouchCard()) {
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			panel.renderImage(field_147501_a.field_147553_e);
			GL11.glEnable(GL11.GL_LIGHTING);
		} else 
			renderText(panel, joinedData, displayWidth, displayHeight);
	}

	private void renderText(TileEntityInfoPanel panel, List<PanelString> joinedData, float displayWidth, float displayHeight) {
		FontRenderer fontRenderer = func_147498_b();
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
		GL11.glScalef(scale, -scale, scale);
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

		GL11.glDisable(GL11.GL_LIGHTING);

		int row = 0;
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorTextHex();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
				fontRenderer.drawString(panelString.textLeft, offsetX - realWidth / 2,
						offsetY - realHeight / 2 + row * lineHeight,
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

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityInfoPanel) te, x, y, z);
	}
}
