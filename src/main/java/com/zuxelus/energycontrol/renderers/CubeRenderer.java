package com.zuxelus.energycontrol.renderers;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.Direction;

public class CubeRenderer { // net.minecraft.client.model.geom.ModelPart
	// timer and thermal monitor: a flat box whose top side is the screen
	public static final CubeRenderer MODEL = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64);
	// side of each ModelBox quad before rotation (see ModelBox constructor)
	static final Direction[] QUAD_SIDES = { Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
	private static final Direction[][][] WORLD_SIDES = new Direction[6][6][];

	private final ModelBox cube;

	private CubeRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
		cube = new ModelBox(x, y, z, dx, dy, dz, textureWidth, textureHeight);
	}

	public void render(PoseStack matrixStack, SubmitNodeCollector collector, RenderType type, int[] light, int combinedOverlay) {
		matrixStack.pushPose();
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		collector.submitCustomGeometry(matrixStack, type, (pose, buffer) -> cube.render(pose, buffer, light, combinedOverlay, 0xFFFFFFFF));
		matrixStack.popPose();
	}

	static class PositionTextureVertex {
		public final Vector3f position;
		public final float textureU;
		public final float textureV;

		public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
			this(new Vector3f(x, y, z), texU, texV);
		}

		public PositionTextureVertex setTextureUV(float texU, float texV) {
			return new PositionTextureVertex(this.position, texU, texV);
		}

		public PositionTextureVertex(Vector3f posIn, float texU, float texV) {
			this.position = posIn;
			this.textureU = texU;
			this.textureV = texV;
		}
	}

	static class TexturedQuad {
		public final PositionTextureVertex[] vertexPositions;
		public final Vector3f normal;

		public TexturedQuad(PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, Direction direction) {
			this.vertexPositions = positionsIn;
			float f = 0.0F / texWidth;
			float f1 = 0.0F / texHeight;
			positionsIn[0] = positionsIn[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
			positionsIn[1] = positionsIn[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
			positionsIn[2] = positionsIn[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
			positionsIn[3] = positionsIn[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
			this.normal = direction.step();
		}

		public void draw(PoseStack.Pose pose, VertexConsumer buffer, int light, int combinedOverlay, int color) {
			Matrix4f matrix4f = pose.pose();
			Vector3f vector3f = pose.transformNormal(normal, new Vector3f());

			float f = vector3f.x();
			float g = vector3f.y();
			float h = vector3f.z();

			for (int i = 0; i < 4; ++i) {
				PositionTextureVertex vertex = vertexPositions[i];
				Vector4f vector4f = matrix4f.transform(new Vector4f(vertex.position.x() / 16.0F, vertex.position.y() / 16.0F, vertex.position.z() / 16.0F, 1.0F));
				buffer.addVertex(vector4f.x(), vector4f.y(), vector4f.z(), color, vertex.textureU, vertex.textureV, combinedOverlay, light, f, g, h);
			}
		}
	}

	private class ModelBox {
		private final TexturedQuad[] quads;

		public ModelBox(float x, float y, float z, float dx, float dy, float dz, float texWidth, float texHeight) {
			quads = new TexturedQuad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			PositionTextureVertex v = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v7 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex v8 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { v6, v2, v3, v7 }, dz + dx, dz, dz + dx + dz, dz + dy, texWidth, texHeight, Direction.EAST);
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { v, v5, v8, v4 }, 0, dz, dz, dz + dy, texWidth, texHeight, Direction.WEST);
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { v6, v5, v, v2 }, dz, 0, dz + dx, dz, texWidth, texHeight, Direction.DOWN);
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { v3, v4, v8, v7 }, dz + dx, dz, dz + dx + dx, 0, texWidth, texHeight, Direction.UP);
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v, v4, v3 }, dz, dz, dz + dx, dz + dy, texWidth, texHeight, Direction.NORTH);
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v7, v8 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH);
		}

		public void render(PoseStack.Pose pose, VertexConsumer buffer, int[] light, int combinedOverlay, int color) {
			for (int n = 0; n < quads.length; ++n)
				quads[n].draw(pose, buffer, light[n], combinedOverlay, color);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static void rotateBlock(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = facing.getAxis().isVertical() ? Direction.SOUTH : Direction.DOWN;

		switch (facing) {
		case UP:
			switch (rotation) {
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(-90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, -1.0F);
			switch (rotation) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			matrixStack.rotate(Axis.XP.rotationDegrees(90.0F));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			switch (rotation) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.rotate(Axis.XP.rotationDegrees(180.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			switch (rotation) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			matrixStack.rotate(Axis.ZP.rotationDegrees(-90.0F));
			matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			switch (rotation) {
			case UP:
				matrixStack.rotate(Axis.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				matrixStack.rotate(Axis.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}
	}

	public static int[] getBlockLight(BlockEntityFacing te) {
		return getBlockLight(te, WORLD_SIDES, CubeRenderer::rotateBlock);
	}

	interface Rotator {
		void rotate(PoseStack matrixStack, Direction facing, Direction rotation);
	}

	// Light of the neighbour block each quad faces, in quad order. The sides each quad faces after the rotation are cached
	static int[] getBlockLight(BlockEntityFacing te, Direction[][][] cache, Rotator rotator) {
		Direction facing = te.getFacing();
		Direction rotation = te.getRotation() == null ? Direction.NORTH : te.getRotation();
		Direction[] sides = cache[facing.get3DDataValue()][rotation.get3DDataValue()];
		if (sides == null) {
			PoseStack matrixStack = new PoseStack();
			rotator.rotate(matrixStack, facing, rotation);
			Matrix3f normal = matrixStack.last().normal();
			sides = new Direction[QUAD_SIDES.length];
			for (int i = 0; i < QUAD_SIDES.length; i++) {
				Vector3f v = normal.transform(new Vector3f(QUAD_SIDES[i].step()));
				sides[i] = Direction.getApproximateNearest(v.x(), v.y(), v.z());
			}
			cache[facing.get3DDataValue()][rotation.get3DDataValue()] = sides;
		}
		int[] light = new int[sides.length];
		for (int i = 0; i < sides.length; i++)
			light[i] = LightCoordsUtil.getLightCoords(te.getLevel(), te.getBlockPos().relative(sides[i]));
		return light;
	}
}
