package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TERemoteThermalMonitorRenderer extends TileEntitySpecialRenderer<TileEntityRemoteThermalMonitor> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/blocks/remote_thermal_monitor/all.png");

	@Override
	public void render(TileEntityRemoteThermalMonitor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		CubeRenderer.rotateBlock(te.getFacing());

		if (destroyStage > -1) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			CubeRenderer.DESTROY.render(0.03125F);
		} else {
			bindTexture(TEXTURE);
			CubeRenderer.MODEL.render(0.03125F);
	
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F); // y axis
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // x axis
			GlStateManager.translate(0.0F, -0.5F, 0.001F);
	
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
				bufferbuilder.pos(1, 0.75, 0).tex(0.25,0.1875).endVertex();
				bufferbuilder.pos(rate, 0.75, 0).tex(rate * 0.25,0.1875).endVertex();
				tessellator.draw();
			}
	
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -0.125F, 0.0F);
			GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
	
			String text = Integer.toString(level);
			getFontRenderer().drawString(text, -getFontRenderer().getStringWidth(text) / 2, -getFontRenderer().FONT_HEIGHT, 0x000000);
		}
		GlStateManager.popMatrix();
	}
}
