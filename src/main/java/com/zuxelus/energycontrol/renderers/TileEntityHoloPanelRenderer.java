package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zuxelus.energycontrol.ClientTickHandler;
import com.zuxelus.energycontrol.api.IHasBars;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanel;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;

public class TileEntityHoloPanelRenderer extends TileEntityRenderer<TileEntityHoloPanel> {

	public TileEntityHoloPanelRenderer(TileEntityRendererDispatcher te) {
		super(te);
	}

	@Override
	public void render(TileEntityHoloPanel te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (partialTicks != -1) {
			ClientTickHandler.holo_panels.add(te);
			//return;
		}
		matrixStack.push();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.5F, 0.0F);
			break;
		case SOUTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, -0.5F, -1.0F);
			break;
		case DOWN:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.5F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, -0.5F, 0.0F);
			break;
		}

		/*int color = 2;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}*/
		if (te.getPowered()) {
			List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
			drawText(te, partialTicks, joinedData, matrixStack, buffer, combinedLight);
		}
		matrixStack.pop();
	}

	private void drawText(TileEntityHoloPanel panel, float partialTicks, List<PanelString> joinedData, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight) {
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

		matrixStack.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		matrixStack.rotate(Vector3f.XP.rotationDegrees(-90));
		switch(panel.getFacing())
		{
		case NORTH:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
			break;
		case SOUTH:
			break;
		case WEST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(-90));
			break;
		case EAST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
			break;
		}
		float imageWidth = 0.475F + (displayWidth - 0.875F) / 2F;
		float imageHeight = 0.5F + (power - 1) / 2F;
		if (partialTicks == -1) {
			IHasBars.drawTransparentRect(matrixStack, imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
			IHasBars.drawTransparentRect(matrixStack, imageWidth, imageHeight, -imageWidth, -imageHeight, -0.0001F, 0x40AADDDD);
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
		} else if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F * (power + 1) / 2);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorTextHex();
			TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, renderDispatcher.getFontRenderer(), buffer, combinedLight);
		}
	}
}
