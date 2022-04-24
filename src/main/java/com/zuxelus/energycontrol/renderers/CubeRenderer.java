package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CubeRenderer {
	private boolean compiled;
	private int displayList;
	private CubeBox cube;
	
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
		cube = new CubeBox(x, y, z, dx, dy, dz, textureWidth, textureHeight, faceTexU, faceTexV, offset.leftTop, offset.leftBottom, offset.rightTop, offset.rightBottom);
	}

	@SideOnly(Side.CLIENT)
	public void render(float scale) {
		if (!compiled)
			compileDisplayList(scale);
		GlStateManager.callList(this.displayList);
	}

	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);
		VertexBuffer bufferbuilder = Tessellator.getInstance().getBuffer();
		cube.render(bufferbuilder, scale);
		GlStateManager.glEndList();
		compiled = true;
	}

	private static class CubeBox {
		private final TexturedQuad[] quadList;

		public CubeBox(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight, int faceTexU, int faceTexV, float leftTop, float leftBottom, float rightTop, float rightBottom) {
			quadList = new TexturedQuad[6];
			float f = x + (float) dx;
			float f1 = y + (float) dy;
			float f2 = z + (float) dz;

			PositionTextureVertex v7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex v = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			PositionTextureVertex v1 = new PositionTextureVertex(f, f1 - leftTop, z, 8.0F, 8.0F);
			PositionTextureVertex v2 = new PositionTextureVertex(x, f1 - leftBottom, z, 8.0F, 0.0F);
			PositionTextureVertex v3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			PositionTextureVertex v4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			PositionTextureVertex v5 = new PositionTextureVertex(f, f1 - rightTop, f2, 8.0F, 8.0F);
			PositionTextureVertex v6 = new PositionTextureVertex(x, f1 - rightBottom, f2, 8.0F, 0.0F);
			quadList[0] = new TexturedQuad(new PositionTextureVertex[] { v4, v, v1, v5 }, dz + dx, dz, dz + dx + dz, dz + dy, textureWidth, textureHeight);
			quadList[1] = new TexturedQuad(new PositionTextureVertex[] { v7, v3, v6, v2 }, 0, dz, dz, dz + dy, textureWidth, textureHeight);
			quadList[2] = new TexturedQuad(new PositionTextureVertex[] { v4, v3, v7, v }, dz, 0, dz + dx, dz, textureWidth, textureHeight);
			quadList[3] = new TexturedQuad(new PositionTextureVertex[] { v1, v2, v6, v5 }, faceTexU + dz + dx, faceTexV + dz, faceTexU + dz + dx + dx, faceTexV, textureWidth, textureHeight);
			quadList[4] = new TexturedQuad(new PositionTextureVertex[] { v, v7, v2, v1 }, dz, dz, dz + dx, dz + dy, textureWidth, textureHeight);
			quadList[5] = new TexturedQuad(new PositionTextureVertex[] { v3, v4, v5, v6 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, textureWidth, textureHeight);
		}

		@SideOnly(Side.CLIENT)
		public void render(VertexBuffer renderer, float scale) {
			for (TexturedQuad texturedquad : this.quadList)
				texturedquad.draw(renderer, scale);
		}
	}
}
