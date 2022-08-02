package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TileEntityTimerRenderer extends TileEntitySpecialRenderer<TileEntityTimer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/timer/all.png");
	private static final ResourceLocation TEXTURE_ACTIVE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/timer/active.png");

	@Override
	public void render(TileEntityTimer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		CubeSmallRenderer.rotateBlock(te.getFacing(), te.getRotation());

		if (destroyStage > -1) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			CubeSmallRenderer.DESTROY.render(0.03125F);
		} else {
			bindTexture(te.getIsWorking() ? TEXTURE : TEXTURE_ACTIVE);
			CubeSmallRenderer.MODEL.render(0.03125F);
			String time = te.getTimeString();
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-0.5F, -0.425F, -0.4376F);
			GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
			getFontRenderer().drawString(time, -getFontRenderer().getStringWidth(time) / 2, -getFontRenderer().FONT_HEIGHT, 0x000000);
		}
		GlStateManager.popMatrix();
	}
}
