package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TEAdvancedInfoPanelExtenderRenderer extends TileEntitySpecialRenderer<TileEntityAdvancedInfoPanelExtender> {
	private static final ResourceLocation TEXTUREOFF[];
	private static final ResourceLocation TEXTUREON[];
	private static final CubeRenderer model[];

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/off/alladv%de.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/blocks/info_panel/on/alladv%de.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 192, i * 32 + 64, j * 32 + 64);
	}

	@Override
	public void render(TileEntityAdvancedInfoPanelExtender te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
	    GlStateManager.pushMatrix();
	    GlStateManager.translate((float)x, (float)y, (float)z);
		EnumFacing facing = te.getFacing();
		switch (facing) {
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
		if (te.getPowered())
			bindTexture(TEXTUREON[color]);
		else
			bindTexture(TEXTUREOFF[color]);

		int textureId = te.findTexture();
		byte thickness = te.getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		if (thickness == 16)
			model[textureId].render(0.03125F);
		else
			new CubeRenderer(0, 0, 0, 32, thickness * 2, 32, 128, 192, textureId / 4 * 32 + 64, textureId % 4 * 32 + 64).render(0.03125F);
		GlStateManager.popMatrix();
	}
}
