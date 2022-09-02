package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TEInfoPanelExtenderRenderer extends TileEntitySpecialRenderer<TileEntityInfoPanelExtender> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/info_panel/extender_all.png");

	@Override
	public void render(TileEntityInfoPanelExtender te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te instanceof TileEntityHoloPanelExtender)
			return;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		CubeRenderer.rotateBlock(te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityInfoPanel.GREEN;
		if (destroyStage > -1) {
			bindTexture(DESTROY_STAGES[destroyStage]);
			CubeRenderer.DESTROY.render(0.03125F);
		} else {
			bindTexture(TEXTURE);
			CubeRenderer.MODEL.render(0.03125F);

			bindTexture(TileEntityInfoPanelRenderer.SCREEN);
			TileEntityInfoPanelRenderer.drawFace(te.findTexture(), color, te.getPowered());
		}
		GlStateManager.popMatrix();
	}
}
