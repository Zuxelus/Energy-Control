package com.zuxelus.energycontrol.renderers;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TERemoteThermoRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/remote_thermo/all.png");
	private static final CubeRenderer model = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 64, 0, 0);

	public void renderTileEntityAt(TileEntityRemoteThermo te, double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		ForgeDirection facing = te.getFacingForge();
		switch (facing) {
		case UP:
			break;
		case NORTH:
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-1.0F, 0.0F, 0.0F);
			break;
		case DOWN:
			GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			break;
		}
		bindTexture(TEXTURE);
		model.render(0.03125F);

		GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, -0.5F, 1.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			double rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((double) heat / level, 1)) * 16) / (double) 16;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			// vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			tessellator.addVertexWithUV(rate, 0, 0, rate * 0.25, 0);
			tessellator.addVertexWithUV(1, 0, 0, 0.25, 0);
			tessellator.addVertexWithUV(1, 0.375, 0, 0.25, 0.1875);
			tessellator.addVertexWithUV(rate, 0.375, 0, rate * 0.25, 0.1875);
			tessellator.draw();
		}

		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-0.5F, -0.125F, 0.0F);
		GL11.glScalef(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		func_147498_b().drawString(text, -func_147498_b().getStringWidth(text) / 2, -func_147498_b().FONT_HEIGHT, 0x000000);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityRemoteThermo) te, x, y, z);
	}
}
