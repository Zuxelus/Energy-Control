package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CubeRenderer {
	private boolean compiled;
	private int displayList;
	private CubeBox cube;

	public CubeRenderer(float offX, float offY, float offZ, int width, int height, int depth, float textureWidth, float textureHeight, int faceOffsetX, int faceOffsetY) {
		cube = new CubeBox(offX, offY, offZ, width, height, depth, 0.0F, textureWidth, textureHeight, faceOffsetX, faceOffsetY);
	}

	@SideOnly(Side.CLIENT)
	public void render(float scale) {
		if (!this.compiled)
			this.compileDisplayList(scale);
		GlStateManager.callList(this.displayList);
	}

	@SideOnly(Side.CLIENT)
	private void compileDisplayList(float scale) {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GlStateManager.glNewList(this.displayList, 4864);
		BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
		cube.render(bufferbuilder, scale);
		GlStateManager.glEndList();
		this.compiled = true;
	}

	private class CubeBox {
		private final PositionTextureVertex[] vertexPositions;
		private final TexturedQuad[] quadList;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;

		public CubeBox(float x, float y, float z, int dx, int dy, int dz, float delta, float textureWidth, float textureHeight, int faceTexU, int faceTexV) {
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float) dx;
			this.posY2 = y + (float) dy;
			this.posZ2 = z + (float) dz;
			this.vertexPositions = new PositionTextureVertex[8];
			this.quadList = new TexturedQuad[6];
			float f = x + (float) dx;
			float f1 = y + (float) dy;
			float f2 = z + (float) dz;
			x = x - delta;
			y = y - delta;
			z = z - delta;
			f = f + delta;
			f1 = f1 + delta;
			f2 = f2 + delta;

    		PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
    		PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
    		PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
    		PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
    		PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
    		PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
    		PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
    		PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
    		this.vertexPositions[0] = positiontexturevertex7;
    		this.vertexPositions[1] = positiontexturevertex;
    		this.vertexPositions[2] = positiontexturevertex1;
    		this.vertexPositions[3] = positiontexturevertex2;
    		this.vertexPositions[4] = positiontexturevertex3;
    		this.vertexPositions[5] = positiontexturevertex4;
    		this.vertexPositions[6] = positiontexturevertex5;
    		this.vertexPositions[7] = positiontexturevertex6;
    		this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5 }, dz + dx, dz, dz + dx + dz, dz + dy, textureWidth, textureHeight);
    		this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2 }, 0, dz, dz, dz + dy, textureWidth, textureHeight);
    		this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex }, dz, 0, dz + dx, dz, textureWidth, textureHeight);
    		this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5 }, faceTexU + dz + dx, faceTexV + dz, faceTexU + dz + dx + dx, faceTexV, textureWidth, textureHeight);
    		this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1 }, dz, dz, dz + dx, dz + dy, textureWidth, textureHeight);
    		this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6 }, dz + dx + dz, dz, dz + dx + dz + dx, dz + dy, textureWidth, textureHeight);
    	}

    	@SideOnly(Side.CLIENT)
    	public void render(BufferBuilder renderer, float scale) {
    		for (TexturedQuad texturedquad : this.quadList)
    			texturedquad.draw(renderer, scale);
    	}
    }
}