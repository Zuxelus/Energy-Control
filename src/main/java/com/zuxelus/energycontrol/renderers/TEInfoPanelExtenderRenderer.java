package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.Identifier;

public class TEInfoPanelExtenderRenderer extends TileRenderer<TileEntityInfoPanelExtender> {
	private static final Identifier TEXTURE = Identifier.parse(EnergyControl.MODID + ":textures/block/info_panel/extender_all.png");

	public TEInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	protected void render(TileEntityInfoPanelExtender te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = CubeRenderer.getBlockLight(te);
		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityInfoPanel.GREEN;
		RenderType vertexBuilder = RenderTypes.entityCutout(TEXTURE);
		CubeRenderer.MODEL.render(matrixStack, buffer, vertexBuilder, light, combinedOverlay);
		TileEntityInfoPanelRenderer.drawFace(matrixStack, buffer, te.findTexture(), color, te.getPowered(), light[CubeRenderer.FACE], combinedOverlay);
		matrixStack.popPose();
	}

}
