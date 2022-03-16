package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class TERemoteThermoRenderer extends TileEntityRenderer<TileEntityRemoteThermalMonitor> {
	private static int[][] sides = new int[][] { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 4, 5, 1, 0, 3, 2 },
		{ 4, 5, 0, 1, 2, 3 }, { 4, 0, 3, 2, 1, 5 }, { 4, 1, 2, 3, 0, 5 } };
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/block/remote_thermo/all.png");
	private static final CubeRenderer model = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 64, 0, 0);

	public static int[] getBlockLight(TileEntityFacing te) {
		int[] light = new int[6];
		light[sides[te.getFacing().get3DDataValue()][0]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.DOWN));
		light[sides[te.getFacing().get3DDataValue()][1]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.UP));
		light[sides[te.getFacing().get3DDataValue()][2]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.WEST));
		light[sides[te.getFacing().get3DDataValue()][3]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.EAST));
		light[sides[te.getFacing().get3DDataValue()][4]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.NORTH));
		light[sides[te.getFacing().get3DDataValue()][5]] = WorldRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.SOUTH));
		return light;
	}

	public TERemoteThermoRenderer(TileEntityRendererDispatcher te) {
		super(te);
	}

	@Override
	public void render(TileEntityRemoteThermalMonitor te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		case DOWN:
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
			break;
		}

		IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		model.render(matrixStack, vertexBuilder, getBlockLight(te), combinedOverlay);

		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
		matrixStack.translate(0.0F, -0.5F, 1.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuilder();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			Matrix4f matrix = matrixStack.last().pose();
			bufferbuilder.vertex(matrix, rate, 0, 0).uv(rate * 0.25F, 0).endVertex();
			bufferbuilder.vertex(matrix, 1, 0, 0).uv(0.25F, 0).endVertex();
			bufferbuilder.vertex(matrix, 1.0F, 0.375F, 0).uv(0.25F, 0.1875F).endVertex();
			bufferbuilder.vertex(matrix, rate, 0.375F, 0).uv(rate * 0.25F, 0.1875F).endVertex();
			tessellator.end();
		}

		matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		FontRenderer fontRenderer = renderer.getFont();
		fontRenderer.drawInBatch(text, -fontRenderer.width(text) / 2, -fontRenderer.lineHeight, 0x000000, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
		matrixStack.popPose();
	}
}
