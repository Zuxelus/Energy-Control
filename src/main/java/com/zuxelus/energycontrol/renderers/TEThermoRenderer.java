package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TEThermoRenderer extends TileEntitySpecialRenderer<TileEntityThermo> {
	private static final ResourceLocation TEXTURE0 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all0.png");
	private static final ResourceLocation TEXTURE1 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all1.png");
	private static final ResourceLocation TEXTURE2 = new ResourceLocation(
			EnergyControl.MODID + ":textures/blocks/thermal_monitor/all2.png");
	private final MediumBoxModel model = new MediumBoxModel();

	@Override
	public void render(TileEntityThermo te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		EnumFacing facing = te.getFacing();
		switch (facing) {
		case UP:
			break;
		case NORTH:
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, -1.0F, -1.0F);
			break;
		case SOUTH:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, -1.0F);
			break;
		case EAST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, 0.0F, -1.0F);
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
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.03125F);
		int value = te.getHeatLevel();
		GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.5F, 0.45F, -0.4376F);
		GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
		getFontRenderer().drawString(String.valueOf(value), -getFontRenderer().getStringWidth(String.valueOf(value)) / 2, -getFontRenderer().FONT_HEIGHT, 0x000000);	
		GlStateManager.popMatrix();
	}
}
