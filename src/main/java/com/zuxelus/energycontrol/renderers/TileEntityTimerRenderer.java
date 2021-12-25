package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class TileEntityTimerRenderer implements BlockEntityRenderer<TileEntityTimer> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/block/timer/all.png");
	private static final Identifier TEXTURE_ACTIVE = new Identifier(EnergyControl.MODID + ":textures/block/timer/active.png");
	private static final CubeRenderer model = new CubeRenderer(2, 0, 2, 28, 14, 28, 128, 64, 0, 0);
	private final TextRenderer font;

	public TileEntityTimerRenderer(Context ctx) {
		font = ctx.getTextRenderer();
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void render(TileEntityTimer te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		switch (te.getFacing()) {
		case UP:
			switch (te.getRotation()) {
			case NORTH:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				break;
			case WEST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case NORTH:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			matrixStack.translate(-1.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case EAST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case WEST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case SOUTH:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case EAST:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case DOWN:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case NORTH:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case SOUTH:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		case EAST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			matrixStack.translate(-1.0F, 0.0F, -1.0F);
			switch (te.getRotation()) {
			case UP:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.translate(-1.0F, 0.0F, -1.0F);
				break;
			case DOWN:
				break;
			case SOUTH:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				break;
			case NORTH:
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			}
			break;
		}

		VertexConsumer vertexBuilder = te.getIsWorking() ? buffer.getBuffer(RenderLayer.getEntitySolid(TEXTURE_ACTIVE)) : buffer.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
		model.render(matrixStack, vertexBuilder, TileEntityInfoPanelRenderer.getBlockLight(te), combinedOverlay);
		String time = te.getTimeString();
		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		matrixStack.translate(0.5F, 0.575F, -0.4376F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);
		font.draw(time, -font.getWidth(time) / 2, -font.fontHeight, 0x000000, false, matrixStack.peek().getPositionMatrix(), buffer, false, 0, combinedLight);
		matrixStack.pop();
	}

	@Override
	public int getRenderDistance() {
		return 65536;
	}
}
