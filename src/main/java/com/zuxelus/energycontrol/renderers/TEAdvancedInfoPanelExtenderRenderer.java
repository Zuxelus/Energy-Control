package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class TEAdvancedInfoPanelExtenderRenderer implements BlockEntityRenderer<TileEntityAdvancedInfoPanelExtender> {
	private static final ResourceLocation[] TEXTUREOFF;
	private static final ResourceLocation[] TEXTUREON;
	private static final CubeRenderer[] model;

	static {
		TEXTUREOFF = new ResourceLocation[16];
		TEXTUREON = new ResourceLocation[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/off/alladv%de.png", i));
			TEXTUREON[i] = new ResourceLocation(
					EnergyControl.MODID + String.format(":textures/block/info_panel/on/alladv%de.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

	public TEAdvancedInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	public void render(TileEntityAdvancedInfoPanelExtender te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		int[] light = TileEntityInfoPanelRenderer.getBlockLight(te);
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 6;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 6;
		}
		VertexConsumer vertexBuilder;
		if (te.getPowered())
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREON[color]));
		else
			vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTUREOFF[color]));

		int textureId = te.findTexture();
		byte thickness = te.getThickness();
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		int rotateHor = te.getRotateHor() / 7;
		int rotateVert = te.getRotateVert() / 7; 
		Screen screen = te.getScreen();
		if (screen == null) {
			if (thickness == 16 && rotateHor == 0 && rotateVert == 0)
				model[textureId].render(matrixStack, vertexBuilder, light, combinedOverlay);
		} else {
			if (thickness == 16 && rotateHor == 0 && rotateVert == 0)
				model[textureId].render(matrixStack, vertexBuilder, light, combinedOverlay);
			else {
				RotationOffset offset = new RotationOffset(thickness * 2, rotateHor, rotateVert);
				new CubeRenderer(textureId / 4 * 32 + 64, textureId % 4 * 32 + 64, offset.addOffset(screen, te.getBlockPos(), te.getFacing(), te.getRotation())).render(matrixStack, vertexBuilder, light, combinedOverlay);;
			}
		}
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
