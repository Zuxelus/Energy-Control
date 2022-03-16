package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class TERemoteThermoRenderer extends TileEntityRenderer<TileEntityRemoteThermalMonitor> {
	private static int[][] sides = new int[][] { { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 4, 5, 1, 0, 3, 2 },
		{ 4, 5, 0, 1, 2, 3 }, { 4, 0, 3, 2, 1, 5 }, { 4, 1, 2, 3, 0, 5 } };
	private static final ResourceLocation TEXTURE = new ResourceLocation(EnergyControl.MODID, "textures/block/remote_thermo/all.png");
	private static final CubeRenderer model = new CubeRenderer(0, 0, 0, 32, 32, 32, 128, 64, 0, 0);

	public static int[] getBlockLight(TileEntityFacing te) {
		int[] light = new int[6];
		light[sides[te.getFacing().getIndex()][0]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.DOWN));
		light[sides[te.getFacing().getIndex()][1]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.UP));
		light[sides[te.getFacing().getIndex()][2]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.WEST));
		light[sides[te.getFacing().getIndex()][3]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.EAST));
		light[sides[te.getFacing().getIndex()][4]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.NORTH));
		light[sides[te.getFacing().getIndex()][5]] = WorldRenderer.getCombinedLight(te.getWorld(), te.getPos().offset(Direction.SOUTH));
		return light;
	}

	public TERemoteThermoRenderer(TileEntityRendererDispatcher te) {
		super(te);
	}

	@Override
	public void render(TileEntityRemoteThermalMonitor te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		case DOWN:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case WEST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
			break;
		}

		IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.getEntitySolid(TEXTURE));
		model.render(matrixStack, vertexBuilder, getBlockLight(te), combinedOverlay);

		matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
		matrixStack.translate(0.0F, -0.5F, 1.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			Matrix4f matrix = matrixStack.getLast().getMatrix();
			bufferbuilder.pos(matrix, rate, 0, 0).tex(rate * 0.25F, 0).endVertex();
			bufferbuilder.pos(matrix, 1, 0, 0).tex(0.25F, 0).endVertex();
			bufferbuilder.pos(matrix, 1.0F, 0.375F, 0).tex(0.25F, 0.1875F).endVertex();
			bufferbuilder.pos(matrix, rate, 0.375F, 0).tex(rate * 0.25F, 0.1875F).endVertex();
			tessellator.draw();
		}

		matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		FontRenderer fontRenderer = renderDispatcher.getFontRenderer();
		fontRenderer.renderString(text, -fontRenderer.getStringWidth(text) / 2, -fontRenderer.FONT_HEIGHT, 0x000000, false, matrixStack.getLast().getMatrix(), buffer, false, 0, combinedLight);
		matrixStack.pop();
	}
}
