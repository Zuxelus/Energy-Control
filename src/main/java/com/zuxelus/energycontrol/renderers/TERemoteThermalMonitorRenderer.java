package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class TERemoteThermalMonitorRenderer implements BlockEntityRenderer<TileEntityRemoteThermalMonitor> {
	private static int[][] sides = new int[][] { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 4, 5, 1, 0, 3, 2 },
		{ 4, 5, 0, 1, 2, 3 }, { 4, 0, 3, 2, 1, 5 }, { 4, 1, 2, 3, 0, 5 } };
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/block/remote_thermal_monitor/all.png");
	private final Font font;

	public static int[] getBlockLight(BlockEntityFacing te) {
		int[] light = new int[6];
		int[][] sides = new int[][] { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 4, 5, 1, 0, 3, 2 },
			{ 4, 5, 0, 1, 2, 3 }, { 4, 0, 3, 2, 1, 5 }, { 2, 3, 5, 4, 1, 0 } };
		light[sides[te.getFacing().get3DDataValue()][0]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.DOWN));
		light[sides[te.getFacing().get3DDataValue()][1]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.UP));
		light[sides[te.getFacing().get3DDataValue()][2]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.WEST));
		light[sides[te.getFacing().get3DDataValue()][3]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.EAST));
		light[sides[te.getFacing().get3DDataValue()][4]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.NORTH));
		light[sides[te.getFacing().get3DDataValue()][5]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.SOUTH));
		return light;
	}

	public TERemoteThermalMonitorRenderer(Context ctx) {
		font = ctx.getFont();
	}

	@Override
	public void render(TileEntityRemoteThermalMonitor te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();

		CubeRenderer.rotateBlock(matrixStack, te.getFacing(), te.getRotation());

		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		CubeRenderer.MODEL.render(matrixStack, vertexBuilder, TileEntityInfoPanelRenderer.getBlockLight(te), combinedOverlay);
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.translate(0.0F, -0.5F, 0.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16;
			
			RenderSystem.enableDepthTest();
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuilder();
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			Matrix4f matrix = matrixStack.last().pose();
			bufferbuilder.vertex(matrix, rate, 0, 0).uv(rate * 0.25F, 0).endVertex();
			bufferbuilder.vertex(matrix, 1, 0, 0).uv(0.25F, 0).endVertex();
			bufferbuilder.vertex(matrix, 1.0F, 0.75F, 0).uv(0.25F, 0.1875F).endVertex();
			bufferbuilder.vertex(matrix, rate, 0.75F, 0).uv(rate * 0.25F, 0.1875F).endVertex();
			tessellator.end();
		}

		matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		font.drawInBatch(text, -font.width(text) / 2, -font.lineHeight, 0x000000, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
		matrixStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 65536;
	}
}
