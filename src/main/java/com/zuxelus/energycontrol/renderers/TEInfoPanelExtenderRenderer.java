package com.zuxelus.energycontrol.renderers;

import org.lwjgl.opengl.GL11;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TEInfoPanelExtenderRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation[] TEXTUREOFF;
	private static final ResourceLocation[] TEXTUREON;
	private static final CubeRenderer[] model;

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/off/all%de.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/on/all%de.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

	@SuppressWarnings("incomplete-switch")
	public void renderTileEntityAt(TileEntityInfoPanelExtender te, double x, double y, double z) {
		if (te instanceof TileEntityHoloPanelExtender)
			return;
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
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityInfoPanelExtender) te, x, y, z);
	}
}
