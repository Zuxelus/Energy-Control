package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TERemoteThermoRenderer extends TileEntitySpecialRenderer<TileEntityRemoteThermo> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/blocks/remote_thermo/all.png");
	private static final CubeRenderer model = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 64, 0, 0);

	@Override
	public void render(TileEntityRemoteThermo te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
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
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, 0.0F, 0.0F);
			break;
		case DOWN:
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			break;
		}
		bindTexture(TEXTURE);
		model.render(0.03125F);

		GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translate(0.0F, -0.5F, 1.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			double rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((double) heat / level, 1)) * 16) / (double) 16;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(rate, 0, 0).tex(rate * 0.25,0).endVertex();
			bufferbuilder.pos(1, 0, 0).tex(0.25,0).endVertex();
			bufferbuilder.pos(1, 0.375, 0).tex(0.25,0.1875).endVertex();
			bufferbuilder.pos(rate, 0.375, 0).tex(rate * 0.25,0.1875).endVertex();
			tessellator.draw();
		}

		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(-0.5F, -0.125F, 0.0F);
		GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		getFontRenderer().drawString(text, -getFontRenderer().getStringWidth(text) / 2, -getFontRenderer().FONT_HEIGHT, 0x000000);
		GlStateManager.popMatrix();
	}
}
