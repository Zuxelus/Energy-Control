package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TEAdvancedInfoPanelExtenderRenderer implements BlockEntityRenderer<TileEntityAdvancedInfoPanelExtender> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID + ":textures/block/info_panel/extender_advanced_all.png");

	public TEAdvancedInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	public void render(TileEntityAdvancedInfoPanelExtender te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = TileEntityInfoPanelRenderer.getBlockLight(te);

		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		int color = te.getColored() ? te.getColorBackground() : TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND;
		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));

		int textureId = te.findTexture();
		byte thickness = te.getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.getRotateHor() / 7;
		int rotateVert = te.getRotateVert() / 7;

		if (thickness == 16 && rotateHor == 0 && rotateVert == 0) {
			CubeRenderer.MODEL.render(matrixStack, vertexBuilder, light, combinedOverlay);
			VertexConsumer vertexScreen = buffer.getBuffer(RenderType.entitySolid(TileEntityInfoPanelRenderer.SCREEN));
			TileEntityInfoPanelRenderer.drawFace(matrixStack.last(), vertexScreen, te.findTexture(), color, te.getPowered(), combinedLight, combinedOverlay);
		} else {
			Screen screen = te.getScreen();
			if (screen != null) {
				RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert).addOffset(screen, te.getBlockPos(), te.getFacing(), te.getRotation());
				CubeRenderer.getModel(offset).render(matrixStack, vertexBuilder, light, combinedOverlay);
				VertexConsumer vertexScreen = buffer.getBuffer(RenderType.entitySolid(TileEntityInfoPanelRenderer.SCREEN));
				CubeRenderer.getFaceModel(offset, textureId).render(matrixStack, vertexBuilder, light, combinedOverlay, color);
			}
		}
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
