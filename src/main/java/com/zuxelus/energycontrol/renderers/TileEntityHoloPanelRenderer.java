package com.zuxelus.energycontrol.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.ClientTickHandler;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityHoloPanelRenderer extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntityHoloPanel te, double x, double y, double z, float partialTicks) {
		if (partialTicks != -1) {
			ClientTickHandler.holo_panels.add(te);
			//return;
		}
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		switch (te.getFacingForge()) {
		case UP:
			break;
		case NORTH:
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.5F, 0.0F);
			break;
		case SOUTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.5F, -1.0F);
			break;
		case DOWN:
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(0.0F, -1.5F, 0.0F);
			break;
		case EAST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-1.0F, -0.5F, 0.0F);
			break;
		}
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, partialTicks, joinedData);
		}
		GL11.glPopMatrix();
	}

	@SuppressWarnings("incomplete-switch")
	private void drawText(TileEntityHoloPanel panel, float partialTicks, List<PanelString> joinedData) {
		Screen screen = panel.getScreen();
		int power = panel.getPower();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = power - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacingForge()) {
			case NORTH:
				dz = (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
				dz = dz - power + 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case SOUTH:
				dz = - (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = panel.xCoord - screen.maxX - screen.minX + panel.xCoord;
				dz = dz + power - 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case WEST:
				dz = panel.zCoord - screen.maxZ + panel.zCoord - screen.minZ;
				dy = (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = dy - power + 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			case EAST:
				dz = panel.zCoord - screen.maxZ + panel.zCoord - screen.minZ;
				dy = - (panel.yCoord - screen.maxY - screen.minY + panel.yCoord);
				dy = dy + power - 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			}
		}

		GL11.glTranslatef(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getFacingForge())
		{
		case NORTH:
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			break;
		case SOUTH:
			break;
		case WEST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			break;
		case EAST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			break;
		}
		float imageWidth = 0.475F + (displayWidth - 0.875F) / 2F;
		float imageHeight = 0.5F + (power - 1) / 2F;
		if (partialTicks == -1) {
			IHasBars.drawTransparentRect(imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			IHasBars.drawTransparentRect(imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		} else if (joinedData != null) {
			GL11.glTranslatef(0, 0, 0.0002F * (power + 1) / 2);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorText();
			TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, func_147498_b());
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityHoloPanel) te, x, y, z, partialTicks);
	}
}
