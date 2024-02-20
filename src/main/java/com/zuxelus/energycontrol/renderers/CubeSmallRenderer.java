package com.zuxelus.energycontrol.renderers;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.renderers.CubeRenderer.PositionTextureVertex;
import com.zuxelus.energycontrol.renderers.CubeRenderer.TexturedQuad;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeSmallRenderer {
	public static final CubeSmallRenderer MODEL = new CubeSmallRenderer(2, 0, 2, 28, 14, 28, 128, 128);
	private CubeSmallBox cube;

	public CubeSmallRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
		cube = new CubeSmallBox(x, y, z, dx, dy, dz, textureWidth, textureHeight);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
		cube.render(matrixStack, buffer, light, combinedOverlay);
	}

	private static class CubeSmallBox {
		private final TexturedQuad[] quads;

		public CubeSmallBox(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
			quads = new TexturedQuad[6];
			float f = x + (float) dx;
			float f1 = y + (float) dy;
			float f2 = z + (float) dz;

			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex v = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { v5, v4, v, v1 }, 0, dy, dy, dy + dx, textureWidth, textureHeight, Direction.WEST); // left
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { v3, v6, v2, v7 }, dy + dx, dy, dy + dx + dy, dy + dx, textureWidth, textureHeight, Direction.EAST); // right
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { v4, v3, v7, v }, dy + dx + dy, dy, dy + dx + dy + dx, dy + dz, textureWidth, textureHeight, Direction.SOUTH); // back
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { v1, v2, v6, v5 }, dy, dy, dy + dx, dy + dz, textureWidth, textureHeight, Direction.NORTH); // face
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, dy, dy + dx, dy + dx, dy + dx + dy, textureWidth, textureHeight, Direction.DOWN); // bottom
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v3, v4, v5, v6 }, dy, 0, dy + dx, dy, textureWidth, textureHeight, Direction.UP); // top
		}

		public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			render(matrixStack.last(), buffer, light, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.scale(2.0F, 2.0F, 2.0F);
		}

		public void render(PoseStack.Pose matrixEntry, VertexConsumer buffer, int[] light, int combinedOverlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = matrixEntry.pose();
			Matrix3f matrix3f = matrixEntry.normal();
			for (int n = 0; n < quads.length; ++n)
				quads[n].draw(matrix3f, matrix4f, buffer, light[n], combinedOverlay, red, green, blue, alpha);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static void rotateBlock(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = Direction.NORTH;

		matrixStack.mulPose(Axis.XP.rotationDegrees(-90)); // x, y, z -> x, z, y

		switch (facing) {
		case UP:
			matrixStack.mulPose(Axis.XP.rotationDegrees(90));
			switch (rotation) {
			case NORTH:
				break;
			case SOUTH:
				matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.YP.rotationDegrees(90));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
			switch (rotation) {
			case NORTH:
				matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.translate(0.0F, -1.0F, -1.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.YP.rotationDegrees(90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(-1.0F, -1.0F, -1.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Axis.ZP.rotationDegrees(180)); // x, z, y
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		case WEST:
			matrixStack.mulPose(Axis.ZP.rotationDegrees(90)); // x, z, y
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Axis.ZP.rotationDegrees(-90)); // x, z, y
			break;
		}
	}

	public static int[] getBlockLight(BlockEntityFacing te) {
		int[] light = new int[6];
		int[][] sides = new int[][] { { 3, 2, 1, 0, 5, 4 }, { 2, 3, 1, 0, 4, 5 }, { 4, 5, 1, 0, 3, 2 },
			{ 4 ,5, 0, 1, 2, 3 }, { 4, 5, 3, 2, 0, 1 }, { 4, 5, 2, 3, 1, 0 } };
		int side = te.getFacing().get3DDataValue();
		if (side == 0)
			light[sides[side][0]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][0]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.DOWN));
		if (side == 1)
			light[sides[side][1]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][1]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.UP));
		if (side == 4)
			light[sides[side][2]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][2]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.WEST));
		if (side == 5)
			light[sides[side][3]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][3]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.EAST));
		if (side == 2)
			light[sides[side][4]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][4]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.NORTH));
		if (side == 3)
			light[sides[side][5]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos());
		else
			light[sides[side][5]] = LevelRenderer.getLightColor(te.getLevel(), te.getBlockPos().relative(Direction.SOUTH));
		return light;
	}
}
