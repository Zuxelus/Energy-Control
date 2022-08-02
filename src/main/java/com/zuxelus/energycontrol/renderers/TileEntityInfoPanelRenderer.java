package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TileEntityInfoPanelRenderer extends TileEntitySpecialRenderer<TileEntityInfoPanel> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/info_panel/panel_all.png");
	public static final ResourceLocation SCREEN = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/info_panel/panel_screen.png");

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
		CubeRenderer.rotateBlock(te.getFacing());

		int color = te.getColored() ? te.getColorBackground() : TileEntityInfoPanel.GREEN;
		if (destroyStage > -1) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			CubeRenderer.DESTROY.render(0.03125F);
		} else {
			bindTexture(TEXTURE);
			CubeRenderer.MODEL.render(0.03125F);
			bindTexture(SCREEN);
			drawFace(te.findTexture(), color, te.getPowered());

			CubeRenderer.rotateBlockText(te.getFacing());

			if (te.getPowered()) {
				List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
				drawText(te, joinedData);
			}
		}
		GlStateManager.popMatrix();
	}

	public static void drawFace(int texture, int color, boolean isPowered) {
		if (isPowered)
			GlStateManager.disableLighting();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		double x = (texture / 4) * 0.25;
		double y = (texture % 4) * 0.25;
		drawPositionColor(bufferbuilder, x, y, x + 0.25, y + 0.25, color);
		tessellator.draw();
		if (isPowered)
			GlStateManager.enableLighting();
	}

	static void drawPositionColor(BufferBuilder builder, double uMin, double vMin, double uMax, double vMax, long color) {
		float f = (color >> 24 & 255) / 255.0F;
		float f1 = (color >> 16 & 255) / 255.0F;
		float f2 = (color >> 8 & 255) / 255.0F;
		float f3 = (color & 255) / 255.0F;
		builder.pos(1, 0, 0).tex(uMin, vMax).color(f1, f2, f3, f).endVertex();
		builder.pos(0, 0, 0).tex(uMax, vMax).color(f1, f2, f3, f).endVertex();
		builder.pos(0, 1, 0).tex(uMax, vMin).color(f1, f2, f3, f).endVertex();
		builder.pos(1, 1, 0).tex(uMin, vMin).color(f1, f2, f3, f).endVertex();
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
					dz = pos.getZ() - screen.maxZ + screen.minZ - pos.getZ();
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = -pos.getZ() + screen.maxZ + screen.minZ - pos.getZ();
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
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
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
