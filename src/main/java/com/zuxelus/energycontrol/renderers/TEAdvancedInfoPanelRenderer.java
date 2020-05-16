package com.zuxelus.energycontrol.renderers;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.CardState;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TEAdvancedInfoPanelRenderer extends TileEntitySpecialRenderer<TileEntityAdvancedInfoPanel> {
	private static final ResourceLocation TEXTUREOFF[];
	private static final ResourceLocation TEXTUREON[];
	private static final CubeRenderer model[];

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/off/alladv%d.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/on/alladv%d.png", i));
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

	@Override
	public void render(TileEntityAdvancedInfoPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
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

		int color = 6;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 6;
		}
		if (te.powered)
			bindTexture(TEXTUREON[color]);
		else
			bindTexture(TEXTUREOFF[color]);

		int textureId = te.findTexture();
		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.rotateHor / 7;
		int rotateVert = te.rotateVert / 7;
		RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert);
		Screen screen = te.getScreen();
		if (thickness == 16 && rotateHor == 0 && rotateVert == 0)
			model[textureId].render(0.03125F);
		else {
			new CubeRenderer(textureId / 4 * 32 + 64, textureId % 4 * 32 + 64, offset.addOffset(screen, te.getPos(), te.getFacing(), te.getRotation())).render(0.03125F);
		}
		if (te.powered) {
			boolean anyCardFound = false;
			List<PanelString> joinedData = te.getPanelStringList(te.getShowLabels());
			if (joinedData != null)
				drawText(te, joinedData, thickness, offset);
		}
		GlStateManager.popMatrix();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, byte thickness, RotationOffset offset) {
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
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
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
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
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

		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getRotation())
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

		double b = Math.atan((offset.leftBottom - offset.rightBottom) / 32.0F / (displayWidth + 0.125F));
		double a = Math.atan(Math.cos(b) * (offset.leftTop - offset.leftBottom) / 32.0F / (displayHeight + 0.125F));
		int i = offset.rotateVert == 0 ? 0 : offset.rotateVert > 0 ? -1 : 1;
		GlStateManager.translate((displayWidth + 0.125F) / 2, (displayHeight + 0.125F) / 2,
				(displayWidth + 0.125F) / 2 * Math.tan(b) + (32 - offset.leftTop + (offset.leftTop - offset.leftBottom) / 2.0F) / 32.0F);
		GlStateManager.rotate((float) Math.toDegrees(b), 0.0F, -1.0F, 0.0F);
		GlStateManager.rotate((float) Math.toDegrees(a), -1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.001F * i, 0.001F);
		displayHeight = (float) (displayHeight / Math.cos(a));
		displayWidth = (float) (displayWidth / Math.cos(b));

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

		GlStateManager.disableLighting();

		int row = 0;
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorTextHex();
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
