package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CubeRenderer {
	public static final CubeRenderer MODEL = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 128, 0, 0, false);
	public static final CubeRenderer DESTROY = new CubeRenderer(0, 0, 0, 32, 32, 32, 32, 32, 0, 0, false);

	private boolean compiled;
	private int displayList;
	private CubeBox cube;

	public CubeRenderer(float offX, float offY, float offZ, int width, int height, int depth, float textureWidth, float textureHeight, int faceOffsetX, int faceOffsetY, boolean faceOnly) {
		this(offX, offY, offZ, width, height, depth, textureWidth, textureHeight, faceOffsetX, faceOffsetY, new RotationOffset(), faceOnly);
	}

	public CubeRenderer(int faceOffsetX, int faceOffsetY, RotationOffset offset, boolean faceOnly) {
		this(0.0F, 0.0F, 0.0F, 32, 32, 32, 128, 128, faceOffsetX, faceOffsetY, offset, faceOnly);
	}

	public CubeRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight, int faceTexU, int faceTexV, RotationOffset offset, boolean faceOnly) {
		cube = new CubeBox(x, y, z, dx, dy, dz, textureWidth, textureHeight, faceTexU, faceTexV, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom, faceOnly);
	}

	@SideOnly(Side.CLIENT)
	public void render(float scale) {
		if (!compiled)
			compileDisplayList(scale);
		GlStateManager.callList(displayList);
	}

	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(displayList, 4864);
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		cube.render(bufferbuilder, scale);
		GlStateManager.glEndList();
		compiled = true;
	}

	@SideOnly(Side.CLIENT)
	public void render(float scale, long color) {
		if (!compiled)
			compileDisplayList(scale, color);
		GlStateManager.callList(displayList);
	}

	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float scale, long color) {
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(displayList, 4864);
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		cube.render(bufferbuilder, scale, color);
		GlStateManager.glEndList();
		compiled = true;
	}

	private static class CubeBox {
		private final TexturedQuad[] quadList;

		public CubeBox(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight, int faceTexU, int faceTexV, float leftTop, float leftBottom, float rightTop, float rightBottom, boolean faceOnly) {
			quadList = faceOnly ? new TexturedQuad[1] : new TexturedQuad[6];
			float f = x + (float) dx;
			float f1 = y + (float) dy;
			float f2 = z + (float) dz;

			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z + rightBottom, 0.0F, 0.0F);
			PositionTextureVertex v = new PositionTextureVertex(f, y, z + leftBottom, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(f, f1, z + leftTop, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, f1, z + rightTop, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
			if (faceOnly) {
				quadList[0] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, faceTexU, faceTexV, faceTexU + dx , faceTexV + dx, textureWidth, textureHeight);
				return;
			}
			quadList[0] = new TexturedQuad(new PositionTextureVertex[] { v1, v5, v4, v }, 0, dz, dz, dz + dy, textureWidth, textureHeight); // left
			quadList[1] = new TexturedQuad(new PositionTextureVertex[] { v6, v2, v7, v3 }, dz + dx, dz, dz + dx + dz, dz + dy, textureWidth, textureHeight); // right
			quadList[2] = new TexturedQuad(new PositionTextureVertex[] { v7, v, v4, v3 }, dz, dz + dz, dz + dx, dz + dz + dz, textureWidth, textureHeight); // bottom
			quadList[3] = new TexturedQuad(new PositionTextureVertex[] { v6, v5, v1, v2 }, dz, 0, dz + dx, dz, textureWidth, textureHeight); // top
			quadList[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, dz, dz, dz + dx, dz + dy, textureWidth, textureHeight); // face
			quadList[5] = new TexturedQuad(new PositionTextureVertex[] { v5, v6, v3, v4 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, textureWidth, textureHeight); // back
		}

		@SideOnly(Side.CLIENT)
		public void render(BufferBuilder renderer, float scale) {
			for (TexturedQuad quad : quadList)
				quad.draw(renderer, scale);
		}

		@SideOnly(Side.CLIENT)
		public void render(BufferBuilder renderer, float scale, long color) { // copy of TexturedQuad.draw
			float c = (color >> 24 & 255) / 255.0F;
			float c1 = (color >> 16 & 255) / 255.0F;
			float c2 = (color >> 8 & 255) / 255.0F;
			float c3 = (color & 255) / 255.0F;

			for (TexturedQuad quad : quadList) {
				Vec3d vec3d = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[0].vector3D);
				Vec3d vec3d1 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[2].vector3D);
				Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
				float f = (float) vec3d2.x;
				float f1 = (float) vec3d2.y;
				float f2 = (float) vec3d2.z;

				/*if (quad.invertNormal) {
					f = -f;
					f1 = -f1;
					f2 = -f2;
				}*/

				renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

				for (int i = 0; i < 4; ++i) {
					PositionTextureVertex v = quad.vertexPositions[i];
					renderer.pos(v.vector3D.x * scale, v.vector3D.y * scale, v.vector3D.z * scale).tex(v.texturePositionX, v.texturePositionY).color(c1, c2, c3, c).normal(f, f1, f2).endVertex();
				}

				Tessellator.getInstance().draw();
			}
		}
	}

	public static void rotateBlock(EnumFacing facing) {
		switch (facing) {
		case UP:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case NORTH:
			break;
		case SOUTH:
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F); // 180 by Y
			GlStateManager.translate(-1.0F, 0.0F, -1.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-1.0F, 0.0F, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, -1.0F, 0.0F);
			GlStateManager.translate(0.0F, 0.0F, -1.0F);
			break;
		}
	}

	public static void rotateBlockText(EnumFacing facing) {
		switch (facing) {
		case UP:
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case DOWN:
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case NORTH:
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.translate(-1.0F, -1.0F, -1.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.0F, -1.0F, -1.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			GlStateManager.translate(-1.0F, -1.0F, 0.0F);
			break;
		}
	}
}
