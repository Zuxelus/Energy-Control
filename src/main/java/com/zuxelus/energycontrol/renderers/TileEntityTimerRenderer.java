package com.zuxelus.energycontrol.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class TileEntityTimerRenderer extends TileEntityRenderer<TileEntityTimer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(
			EnergyControl.MODID + ":textures/block/timer/all.png");
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);

	public TileEntityTimerRenderer(TileEntityRendererDispatcher te) {
		super(te);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void render(TileEntityTimer te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		switch (te.getFacing()) {
		case UP:
			switch (te.getRotation()) {
			case NORTH:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
			matrixStack.translate(-1.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			matrixStack.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
			matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				matrixStack.rotate(Vector3f.YP.rotationDegrees(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}

		IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.getEntitySolid(TEXTURE));
		model.render(matrixStack, vertexBuilder, TileEntityInfoPanelRenderer.getBlockLight(te), combinedOverlay);
		String time = te.getTimeString();
		matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
		matrixStack.translate(0.5F, 0.575F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		FontRenderer fontRenderer = renderDispatcher.getFontRenderer();
		fontRenderer.renderString(time, -fontRenderer.getStringWidth(time) / 2, -fontRenderer.FONT_HEIGHT, 0x000000, false, matrixStack.getLast().getMatrix(), buffer, false, 0, combinedLight);
		matrixStack.pop();
	}
}
