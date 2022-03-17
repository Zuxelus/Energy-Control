package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CubeRenderer { // net.minecraft.client.model.geom.ModelPart
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

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
		cube.render(matrixStack, buffer, light, combinedOverlay);
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
	}

	@OnlyIn(Dist.CLIENT)
	public class ModelBox {
		private final TexturedQuad[] quads;

		public ModelBox(int texOffX, int texOffY, float x, float y, float z, float dx, float dy, float dz, float texWidth, float texHeight, float leftTop, float leftBottom, float rightTop, float rightBottom) {
			quads = new TexturedQuad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			PositionTextureVertex v = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
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
			quads[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v7, v8 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, texWidth, texHeight, Direction.SOUTH);
		}

		public void render(PoseStack matrixStack, VertexConsumer buffer, int[] light, int combinedOverlay) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			render(matrixStack.last(), buffer, light, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.scale(2.0F, 2.0F, 2.0F);
		}

		public void render(PoseStack.Pose matrixEntry, VertexConsumer buffer, int[] light, int combinedOverlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = matrixEntry.pose();
			Matrix3f matrix3f = matrixEntry.normal();

			for (int n = 0; n < quads.length; ++n) {
				TexturedQuad quad = quads[n];
				Vector3f vector3f = quad.normal.copy();
				vector3f.transform(matrix3f);
				float f = vector3f.x();
				float g = vector3f.y();
				float h = vector3f.z();

				for (int i = 0; i < 4; ++i) {
					PositionTextureVertex vertex = quad.vertexPositions[i];
					Vector4f vector4f = new Vector4f(vertex.position.x() / 16.0F, vertex.position.y() / 16.0F, vertex.position.z() / 16.0F, 1.0F);
					vector4f.transform(matrix4f);
					buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, vertex.textureU, vertex.textureV, combinedOverlay, light[n], f, g, h);
				}
			}
		}
	}
}