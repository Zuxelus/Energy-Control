package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TEInfoPanelExtenderRenderer implements BlockEntityRenderer<TileEntityInfoPanelExtender> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/block/info_panel/extender_all.png");

	public TEInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	public void render(TileEntityInfoPanelExtender te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = TileEntityInfoPanelRenderer.getBlockLight(te);
		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityInfoPanel.GREEN;
		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		CubeRenderer.MODEL.render(matrixStack, vertexBuilder, light, combinedOverlay);
		VertexConsumer vertexScreen = buffer.getBuffer(RenderType.entitySolid(TileEntityInfoPanelRenderer.SCREEN));
		TileEntityInfoPanelRenderer.drawFace(matrixStack.last(), vertexScreen, te.findTexture(), color, te.getPowered(), combinedLight, combinedOverlay);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
