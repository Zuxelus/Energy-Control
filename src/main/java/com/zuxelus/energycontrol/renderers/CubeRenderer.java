package com.zuxelus.energycontrol.renderers;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;

public class CubeRenderer { // net.minecraft.client.model.geom.ModelPart
	// side of each ModelBox quad before rotation (see ModelBox constructor)
	static final Direction[] QUAD_SIDES = { Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
	private static final Direction[][][] WORLD_SIDES = new Direction[6][6][];
	private ModelBox cube;

	public CubeRenderer(int faceOffsetX, int faceOffsetY) {
		this(0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 192, faceOffsetX, faceOffsetY);
	}

	public CubeRenderer(float offX, float offY, float offZ, int width, int height, int depth, float textureWidth, float textureHeight, int faceOffsetX, int faceOffsetY) {
		this(offX, offY, offZ, width, height, depth, textureWidth, textureHeight, faceOffsetX, faceOffsetY, new RotationOffset());
	}

	public CubeRenderer(int faceOffsetX, int faceOffsetY, RotationOffset offset) {
		this(0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 192, faceOffsetX, faceOffsetY, offset);
	}

	public CubeRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight, int faceTexU, int faceTexV, RotationOffset offset) {
		cube = new ModelBox(faceTexU, faceTexV, x, y, z, dx, dy, dz, textureWidth, textureHeight, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom);
	}

	@Environment(EnvType.CLIENT)
	public void render(PoseStack matrixStack, SubmitNodeCollector collector, RenderType renderType, int[] light) {
		cube.render(matrixStack, collector, renderType, light);
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public class ModelBox {
		private final TexturedQuad[] quads;

		public ModelBox(int texOffX, int texOffY, float x, float y, float z, float dx, float dy, float dz, float texWidth, float texHeight, float leftTop, float leftBottom, float rightTop, float rightBottom) {
			quads = new TexturedQuad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			PositionTextureVertex vertex = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex vertex2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex vertex3 = new PositionTextureVertex(f, f1 - leftTop, z, 8.0F, 8.0F);
			PositionTextureVertex vertex4 = new PositionTextureVertex(x, f1 - leftBottom, z, 8.0F, 0.0F);
			PositionTextureVertex vertex5 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex vertex6 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex vertex7 = new PositionTextureVertex(f, f1 - rightTop, f2, 8.0F, 8.0F);
			PositionTextureVertex vertex8 = new PositionTextureVertex(x, f1 - rightBottom, f2, 8.0F, 0.0F);
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { vertex6, vertex5, vertex, vertex2 }, dz, 0, dz + dx, dz, texWidth, texHeight, Direction.DOWN);
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { vertex3, vertex4, vertex8, vertex7 }, texOffX + dz + dx, texOffY + dz, texOffX + dz + dx + dx, texOffY, texWidth, texHeight, Direction.UP);
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { vertex, vertex5, vertex8, vertex4 }, 0, dz, dz, dz + dy, texWidth, texHeight, Direction.WEST);
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { vertex2, vertex, vertex4, vertex3 }, dz, dz, dz + dx, dz + dy, texWidth, texHeight, Direction.NORTH);
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { vertex6, vertex2, vertex3, vertex7 }, dz + dx, dz, dz + dx + dz, dz + dy, texWidth, texHeight, Direction.EAST);
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { vertex5, vertex6, vertex7, vertex8 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH);
		}

		public void render(PoseStack matrixStack, SubmitNodeCollector collector, RenderType renderType, int[] light) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			collector.submitCustomGeometry(matrixStack, renderType, (pose, buffer) -> render(pose, buffer, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF));
			matrixStack.scale(2.0F, 2.0F, 2.0F);
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
