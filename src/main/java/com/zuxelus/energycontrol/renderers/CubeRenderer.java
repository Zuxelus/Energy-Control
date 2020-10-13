package com.zuxelus.energycontrol.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

public class CubeRenderer {
	private Cuboid cube;
	
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
		cube = new Cuboid(faceTexU, faceTexV, x, y, z, dx, dy, dz, textureWidth, textureHeight, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom);
	}
	
	@Environment(EnvType.CLIENT)
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
		cube.render(matrices, vertexConsumer, light, overlay);
	}

	@Environment(EnvType.CLIENT)
	static class Vertex {
		public final Vector3f pos;
		public final float u;
		public final float v;

		public Vertex(float x, float y, float z, float u, float v) {
			this(new Vector3f(x, y, z), u, v);
		}

		public Vertex remap(float u, float v) {
			return new Vertex(this.pos, u, v);
		}

		public Vertex(Vector3f vector3f, float u, float v) {
			this.pos = vector3f;
			this.u = u;
			this.v = v;
		}
	}

	@Environment(EnvType.CLIENT)
	static class Quad {
		public final Vertex[] vertices;
		public final Vector3f direction;

		public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, Direction direction) {
			this.vertices = vertices;
			float f = 0.0F / squishU;
			float g = 0.0F / squishV;
			vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
			vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
			vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
			vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
			this.direction = direction.getUnitVector();
		}
	}

	@Environment(EnvType.CLIENT)
	public /*static*/ class Cuboid {
		private final Quad[] sides;

		public Cuboid(int u, int v, float x, float y, float z, float dx, float dy, float dz, float textureWidth, float textureHeight, float leftTop, float leftBottom, float rightTop, float rightBottom) {
			sides = new Quad[6];
			float f = x + dx;
			float f1 = y + dy;
			float f2 = z + dz;
			Vertex vertex = new Vertex(x, y, z, 0.0F, 0.0F);
			Vertex vertex2 = new Vertex(f, y, z, 0.0F, 8.0F);
			Vertex vertex3 = new Vertex(f, f1 - leftTop, z, 8.0F, 8.0F);
			Vertex vertex4 = new Vertex(x, f1 - leftBottom, z, 8.0F, 0.0F);
			Vertex vertex5 = new Vertex(x, y, f2, 0.0F, 0.0F);
			Vertex vertex6 = new Vertex(f, y, f2, 0.0F, 8.0F);
			Vertex vertex7 = new Vertex(f, f1 - rightTop, f2, 8.0F, 8.0F);
			Vertex vertex8 = new Vertex(x, f1 - rightBottom, f2, 8.0F, 0.0F);
			sides[2] = new Quad(new Vertex[] { vertex6, vertex5, vertex, vertex2 }, dz, 0, dz + dx, dz, textureWidth, textureHeight, Direction.DOWN);
			sides[3] = new Quad(new Vertex[] { vertex3, vertex4, vertex8, vertex7 }, u + dz + dx, v + dz, u + dz + dx + dx, v, textureWidth, textureHeight, Direction.UP);
			sides[1] = new Quad(new Vertex[] { vertex, vertex5, vertex8, vertex4 }, 0, dz, dz, dz + dy, textureWidth, textureHeight, Direction.WEST);
			sides[4] = new Quad(new Vertex[] { vertex2, vertex, vertex4, vertex3 }, dz, dz, dz + dx, dz + dy, textureWidth, textureHeight, Direction.NORTH);
			sides[0] = new Quad(new Vertex[] { vertex6, vertex2, vertex3, vertex7 }, dz + dx, dz, dz + dx + dz, dz + dy, textureWidth, textureHeight, Direction.EAST);
			sides[5] = new Quad(new Vertex[] { vertex5, vertex6, vertex7, vertex8 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, textureWidth, textureHeight, Direction.SOUTH);
		}

		public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay) {
			matrices.scale(0.5F, 0.5F, 0.5F);
			render(matrices.peek(), vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
			matrices.scale(2.0F, 2.0F, 2.0F);
		}

		public void render(MatrixStack.Entry matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
			Matrix4f matrix4f = matrices.getModel();
			Matrix3f matrix3f = matrices.getNormal();

			for (int n = 0; n < sides.length; ++n) {
				Quad quad = sides[n];
				Vector3f vector3f = quad.direction.copy();
				vector3f.transform(matrix3f);
				float f = vector3f.getX();
				float g = vector3f.getY();
				float h = vector3f.getZ();

				for (int i = 0; i < 4; ++i) {
					Vertex vertex = quad.vertices[i];
					Vector4f vector4f = new Vector4f(vertex.pos.getX() / 16.0F, vertex.pos.getY() / 16.0F, vertex.pos.getZ() / 16.0F, 1.0F);
					vector4f.transform(matrix4f);
					vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
				}
			}
		}
	}
}
