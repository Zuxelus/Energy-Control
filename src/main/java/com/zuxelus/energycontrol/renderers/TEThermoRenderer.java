package com.zuxelus.energycontrol.renderers;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TEThermoRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation TEXTURE0 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all0.png");
	private static final ResourceLocation TEXTURE1 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all1.png");
	private static final ResourceLocation TEXTURE2 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all2.png");
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);

	public void renderTileEntityAt(TileEntityThermo te, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		switch (te.getFacingForge()) {
		case UP:
			switch (te.getRotation()) {
			case NORTH:
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-1.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}

		switch (te.getStatus()) {
		case 0:
			bindTexture(TEXTURE1);
			break;
		case 1:
			bindTexture(TEXTURE2);
			break;
		default:
			bindTexture(TEXTURE0);
			break;
		}
		model.render(0.03125F);
		int value = te.getHeatLevel();
		GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.5F, 0.45F, -0.4376F);
		GL11.glScalef(0.015625F, 0.015625F, 0.015625F);
		func_147498_b().drawString(String.valueOf(value), -func_147498_b().getStringWidth(String.valueOf(value)) / 2, -func_147498_b().FONT_HEIGHT, 0x000000);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityThermo)te, x, y, z);
	}
}
