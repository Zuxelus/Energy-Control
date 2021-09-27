package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.ClientTickHandler;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;

public class TileEntityHoloPanelRenderer extends TileEntitySpecialRenderer<TileEntityHoloPanel> {

	@Override
	public void render(TileEntityHoloPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (partialTicks != -1) {
			ClientTickHandler.holo_panels.add(te);
			//return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.5F, 0.0F);
			break;
		case SOUTH:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.5F, -1.0F);
			break;
		case DOWN:
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(0.0F, -1.5F, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-1.0F, -0.5F, 0.0F);
			break;
		}
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, partialTicks, joinedData);
		}
		GlStateManager.popMatrix();
	}

	private void drawText(TileEntityHoloPanel panel, float partialTicks, List<PanelString> joinedData) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getPos();
		int power = panel.getPower();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = power - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case NORTH:
				dz = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				dz = dz - power + 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				dz = dz + power - 1F;
				displayWidth += screen.maxX - screen.minX;
				break;
			case WEST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = dy - power + 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = dy + power - 1F;
				displayWidth += screen.maxZ - screen.minZ;
				break;
			}
		}

		GlStateManager.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		switch(panel.getFacing())
		{
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
		float imageWidth = 0.475F + (displayWidth - 0.875F) / 2F;
		float imageHeight = 0.5F + (power - 1) / 2F;
		if (partialTicks == -1) {
			IHasBars.drawTransparentRect(imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			IHasBars.drawTransparentRect(imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		} else if (joinedData != null) {
			GlStateManager.translate(0, 0, 0.0002F * (power + 1) / 2);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorTextHex();
			TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, getFontRenderer());
		}
	}
}
