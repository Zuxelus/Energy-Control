package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class TEAdvancedInfoPanelExtenderRenderer extends TileRenderer<TileEntityAdvancedInfoPanelExtender> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/block/info_panel/extender_advanced_all.png");

	public TEAdvancedInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	protected void render(TileEntityAdvancedInfoPanelExtender te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = CubeRenderer.getBlockLight(te);

		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND;
		RenderType vertexBuilder = RenderTypes.entityCutout(TEXTURE);

		int textureId = te.findTexture();
		byte thickness = te.getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.getRotateHor() / 7;
		int rotateVert = te.getRotateVert() / 7;

		Screen screen = te.getScreen();
		// the plain cube doesn't depend on the screen, so draw it even before the screen is known
		if (screen == null || (thickness == 16 && rotateHor == 0 && rotateVert == 0)) {
			CubeRenderer.MODEL.render(matrixStack, buffer, vertexBuilder, light, combinedOverlay);
			TileEntityInfoPanelRenderer.drawFace(matrixStack, buffer, textureId, color, te.getPowered(), light[CubeRenderer.FACE], combinedOverlay);
		} else {
			RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert).addOffset(screen, te.getBlockPos(), te.getFacing(), te.getRotation());
			CubeRenderer.getModel(offset).render(matrixStack, buffer, vertexBuilder, light, combinedOverlay);
			CubeRenderer.getFaceModel(offset, textureId).render(matrixStack, buffer, RenderTypes.entitySolid(TileEntityInfoPanelRenderer.SCREEN), new int[] { TileEntityInfoPanelRenderer.getScreenLight(te.getPowered(), light[CubeRenderer.FACE]) }, combinedOverlay, color);
		}
		matrixStack.popPose();
	}

}
