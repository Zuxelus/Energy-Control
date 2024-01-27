package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TEAdvancedInfoPanelExtenderRenderer extends TileEntitySpecialRenderer<TileEntityAdvancedInfoPanelExtender> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/blocks/info_panel/extender_advanced_all.png");

	@Override
	public void render(TileEntityAdvancedInfoPanelExtender te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		CubeRenderer.rotateBlock(te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND;
		if (destroyStage > -1)
			bindTexture(DESTROY_STAGES[destroyStage]);
		else
			bindTexture(TEXTURE);

		int textureId = te.findTexture();
		byte thickness = te.getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.getRotateHor() / 7;
		int rotateVert = te.getRotateVert() / 7;

		if (thickness == 16 && rotateHor == 0 && rotateVert == 0) {
			if (destroyStage > -1)
				CubeRenderer.DESTROY.render(0.03125F);
			else {
				CubeRenderer.MODEL.render(0.03125F);
				bindTexture(TileEntityInfoPanelRenderer.SCREEN);
				TileEntityInfoPanelRenderer.drawFace(te.findTexture(), color, te.getPowered());
			}
		} else {
			Screen screen = te.getScreen();
			if (screen != null) {
				RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert).addOffset(screen, te.getPos(), te.getFacing(), te.getRotation());
				if (destroyStage > -1)
					new CubeRenderer(0.0F, 0.0F, 0.0F, 32, 32, 32, 32, 32, 0, 0, offset, false).render(0.03125F);
				else {
					CubeRenderer.getModel(offset).render(0.03125F);
					bindTexture(TileEntityInfoPanelRenderer.SCREEN);
					CubeRenderer.getFaceModel(offset, textureId).render(0.03125F, color);
				}
			}
		}
		GlStateManager.popMatrix();
	}
}
