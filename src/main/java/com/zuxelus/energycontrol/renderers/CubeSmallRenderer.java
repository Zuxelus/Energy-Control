package com.zuxelus.energycontrol.renderers;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CubeSmallRenderer {
	public static final CubeSmallRenderer MODEL = new CubeSmallRenderer(2, 0, 2, 28, 14, 28, 128, 128);
	public static final CubeSmallRenderer DESTROY = new CubeSmallRenderer(2, 0, 2, 28, 14, 28, 32, 32);

	private boolean compiled;
	private int displayList;
	private CubeSmallBox cube;

	public CubeSmallRenderer(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
		cube = new CubeSmallBox(x, y, z, dx, dy, dz, textureWidth, textureHeight);
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

	private static class CubeSmallBox {
		private final TexturedQuad[] quadList;

		public CubeSmallBox(float x, float y, float z, int dx, int dy, int dz, float textureWidth, float textureHeight) {
			quadList = new TexturedQuad[6];
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
			quadList[0] = new TexturedQuad(new PositionTextureVertex[] { v5, v4, v, v1 }, 0, dy, dy, dy + dx, textureWidth, textureHeight); // left
			quadList[1] = new TexturedQuad(new PositionTextureVertex[] { v3, v6, v2, v7 }, dy + dx, dy, dy + dx + dy, dy + dx, textureWidth, textureHeight); // right
			quadList[2] = new TexturedQuad(new PositionTextureVertex[] { v4, v3, v7, v }, dy + dx + dy, dy, dy + dx + dy + dx, dy + dz, textureWidth, textureHeight); // back
			quadList[3] = new TexturedQuad(new PositionTextureVertex[] { v1, v2, v6, v5 }, dy, dy, dy + dx, dy + dz, textureWidth, textureHeight); // face
			quadList[4] = new TexturedQuad(new PositionTextureVertex[] { v2, v1, v, v7 }, dy, dy + dx, dy + dx, dy + dx + dy, textureWidth, textureHeight); // bottom
			quadList[5] = new TexturedQuad(new PositionTextureVertex[] { v3, v4, v5, v6 }, dy, 0, dy + dx, dy, textureWidth, textureHeight); // top
		}

		@SideOnly(Side.CLIENT)
		public void render(BufferBuilder renderer, float scale) {
			for (TexturedQuad quad : quadList)
				quad.draw(renderer, scale);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static void rotateBlock(EnumFacing facing, EnumFacing rotation) {
		if (rotation == null)
			rotation = EnumFacing.NORTH;

		GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F); // x, y, z -> x, z, y

		switch (facing) {
		case UP:
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			switch (rotation) {
			case NORTH:
				break;
			case SOUTH:
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
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
			break;
		case DOWN:
			GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
			switch (rotation) {
			case NORTH:
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(-1.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				GlStateManager.translate(0.0F, -1.0F, -1.0F);
				break;
			case WEST:
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, -1.0F, 0.0F);
				break;
			case EAST:
				GlStateManager.rotate(90.0F, 0.0F, -1.0F, 0.0F);
				GlStateManager.translate(-1.0F, -1.0F, -1.0F);
				break;
			}
			break;
		case NORTH:
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // x, z, y
			GlStateManager.translate(-1.0F, 0.0F, 0.0F);
			break;
		case WEST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F); // x, z, y
			GlStateManager.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F); // x, z, y
			break;
		}
	}
}
