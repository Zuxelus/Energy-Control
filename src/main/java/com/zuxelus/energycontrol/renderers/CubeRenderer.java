package com.zuxelus.energycontrol.renderers;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeRenderer { // net.minecraft.client.model.geom.ModelPart
	public static final CubeRenderer MODEL = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 128, 0, 0, false);
	public static Map<Long, CubeRenderer> LIBRARY = new HashMap<Long, CubeRenderer>();
	public static Map<Long, CubeRenderer> LIBRARY_FACE = new HashMap<Long, CubeRenderer>();

	private ModelBox cube;

	public CubeRenderer(int faceOffsetX, int faceOffsetY) {
		this(0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 192, faceOffsetX, faceOffsetY, false);
	}

	public CubeRenderer(float offX, float offY, float offZ, int width, int height, int depth, float textureWidth, float textureHeight, int faceOffsetX, int faceOffsetY, boolean faceOnly) {
		this(offX, offY, offZ, width, height, depth, textureWidth, textureHeight, faceOffsetX, faceOffsetY, new RotationOffset(), faceOnly);
	}

	public CubeRenderer(int faceOffsetX, int faceOffsetY, RotationOffset offset, boolean faceOnly) {
		this(0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 128, faceOffsetX, faceOffsetY, offset, faceOnly);
	}

	public CubeRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight, int faceTexU, int faceTexV, RotationOffset offset, boolean faceOnly) {
		cube = new ModelBox(faceTexU, faceTexV, x, y, z, dx, dy, dz, textureWidth, textureHeight, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom, faceOnly);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
		cube.render(matrixStack, buffer, light, combinedOverlay);
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay, int color) {
		cube.render(matrixStack, buffer, light, combinedOverlay, color);
	}

	@OnlyIn(Dist.CLIENT)
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

	@OnlyIn(Dist.CLIENT)
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

		public void draw(Matrix3f matrix3f, Matrix4f matrix4f, VertexConsumer buffer, int light, int combinedOverlay, float red, float green, float blue, float alpha) {
			Vector3f vector3f = matrix3f.transform(new Vector3f(normal));

			float f = vector3f.x();
			float g = vector3f.y();
			float h = vector3f.z();

			for (int i = 0; i < 4; ++i) {
				PositionTextureVertex vertex = vertexPositions[i];
				Vector4f vector4f = matrix4f.transform(new Vector4f(vertex.position.x() / 16.0F, vertex.position.y() / 16.0F, vertex.position.z() / 16.0F, 1.0F));
				buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.textureU, vertex.textureV, combinedOverlay, light, f, g, h);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public class ModelBox {
		private final TexturedQuad[] quads;

		public ModelBox(int texOffX, int texOffY, float x, float y, float z, float dx, float dy, float dz, float texWidth, float texHeight, float leftTop, float leftBottom, float rightTop, float rightBottom, boolean faceOnly) {
			quads = faceOnly ? new TexturedQuad[1] : new TexturedQuad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			/*PositionTextureVertex v = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(f, f1 - leftTop, z, 8.0F, 8.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(x, f1 - leftBottom, z, 8.0F, 0.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v7 = new PositionTextureVertex(f, f1 - rightTop, f2, 8.0F, 8.0F);
			PositionTextureVertex v8 = new PositionTextureVertex(x, f1 - rightBottom, f2, 8.0F, 0.0F);
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { v6, v5, v, v2 }, dz, 0, dz + dx, dz, texWidth, texHeight, Direction.DOWN);
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { v3, v4, v8, v7 }, texOffX + dz + dx, texOffY + dz, texOffX + dz + dx + dx, texOffY, texWidth, texHeight, Direction.UP);
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { v, v5, v8, v4 }, 0, dz, dz, dz + dy, texWidth, texHeight, Direction.WEST);
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v, v4, v3 }, dz, dz, dz + dx, dz + dy, texWidth, texHeight, Direction.NORTH);
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { v6, v2, v3, v7 }, dz + dx, dz, dz + dx + dz, dz + dy, texWidth, texHeight, Direction.EAST);
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v7, v8 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH);*/

			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z + rightBottom, 0.0F, 0.0F);
			PositionTextureVertex v = new PositionTextureVertex(f, y, z + leftBottom, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(f, f1, z + leftTop, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, f1, z + rightTop, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			if (faceOnly) {
				quads[0] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, texOffX, texOffY, texOffX + dx , texOffY + dx, texWidth, texHeight, Direction.NORTH);
				return;
			}
			quads[0] = new TexturedQuad(new PositionTextureVertex[] { v1, v5, v4, v }, 0, dz, dz, dz + dy, texWidth, texHeight, Direction.WEST); // left
			quads[1] = new TexturedQuad(new PositionTextureVertex[] { v6, v2, v7, v3 }, dz + dx, dz, dz + dx + dz, dz + dy, texWidth, texHeight, Direction.EAST); // right
			quads[2] = new TexturedQuad(new PositionTextureVertex[] { v7, v, v4, v3 }, dz, dz + dz, dz + dx, dz + dz + dz, texWidth, texHeight, Direction.DOWN); // bottom
			quads[3] = new TexturedQuad(new PositionTextureVertex[] { v6, v5, v1, v2 }, dz, 0, dz + dx, dz, texWidth, texHeight, Direction.UP); // top
			quads[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, dz, dz, dz + dx, dz + dy, texWidth, texHeight, Direction.NORTH); // face
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v3, v4 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH); // back
		}

		public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			render(matrixStack.last(), buffer, light, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.scale(2.0F, 2.0F, 2.0F);
		}

		public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay, int color) {
			float c = (color >> 24 & 255) / 255.0F;
			float c1 = (color >> 16 & 255) / 255.0F;
			float c2 = (color >> 8 & 255) / 255.0F;
			float c3 = (color & 255) / 255.0F;
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			render(matrixStack.last(), buffer, light, combinedOverlay, c1, c2, c3, c);
			matrixStack.scale(2.0F, 2.0F, 2.0F);
		}

		private void render(PoseStack.Pose matrixEntry, VertexConsumer buffer, int[] light, int combinedOverlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = matrixEntry.pose();
			Matrix3f matrix3f = matrixEntry.normal();
			for (int n = 0; n < quads.length; ++n)
				quads[n].draw(matrix3f, matrix4f, buffer, light[n], combinedOverlay, red, green, blue, alpha);
		}
	}

	public static void rotateBlock(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = Direction.NORTH;

		switch (facing) {
		case UP:
			switch(rotation) {
			case NORTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, -1.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(0.0F, -1.0F, -1.0F);
				break;
			}
			break;
		case DOWN:
			switch(rotation) {
			case NORTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(0.0F, 0.0F, 0.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			break;
		case SOUTH:
			matrixStack.mulPose(Axis.YP.rotationDegrees(180)); // 180 by Y
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
	}

	public static void rotateBlockText(PoseStack matrixStack, Direction facing, Direction rotation) {
		if (rotation == null)
			rotation = Direction.NORTH;

		switch (facing) {
		case UP:
			switch(rotation) {
			case NORTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 1.0F);
				break;
			case SOUTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 1.0F);
				break;
			}
			break;
		case DOWN:
			switch(rotation) {
			case NORTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, -1.0F);
				break;
			case WEST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(-90));
				matrixStack.translate(0.0F, -1.0F, -1.0F);
				break;
			case EAST:
				matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
				matrixStack.mulPose(Axis.YP.rotationDegrees(90));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
			matrixStack.mulPose(Axis.YP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.mulPose(Axis.XP.rotationDegrees(90));
			matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
			matrixStack.mulPose(Axis.YP.rotationDegrees(180));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.mulPose(Axis.ZP.rotationDegrees(180));
			matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		}
	}

	public static CubeRenderer getModel(RotationOffset offset) {
		long hash = (long) (offset.leftTop * 1000000 + offset.leftBottom * 10000 + offset.rightTop * 100 + offset.rightBottom);
		if (LIBRARY.containsKey(hash))
			return LIBRARY.get(hash);
		CubeRenderer model = new CubeRenderer(0, 0, offset, false);
		LIBRARY.put(hash, model);
		return model;
	}

	public static CubeRenderer getFaceModel(RotationOffset offset, int textureId) {
		long hash = (long) (textureId * 100000000 + offset.leftTop * 1000000 + offset.leftBottom * 10000 + offset.rightTop * 100 + offset.rightBottom);
		if (LIBRARY_FACE.containsKey(hash))
			return LIBRARY_FACE.get(hash);
		CubeRenderer model = new CubeRenderer(textureId / 4 * 32, textureId % 4 * 32, offset, true);
		LIBRARY_FACE.put(hash, model);
		return model;
	}
}