package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

// The body is a block model (remote_thermo.json); only the heat bar and the heat level text are drawn here
public class TERemoteThermalMonitorRenderer implements BlockEntityRenderer<TileEntityRemoteThermalMonitor> {
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID, "textures/block/remote_thermal_monitor/all.png");
	// the bar image is the top left 32x11 corner of all.png (128x128), shown at 32 pixels per block
	private static final float BAR_HEIGHT = 11 / 32.0F;
	private static final float BAR_U = 0.25F;
	private static final float BAR_V = 11 / 128.0F;
	private static final int FULL_BRIGHT = LightmapTextureManager.MAX_LIGHT_COORDINATE;
	private final TextRenderer font;

	public TERemoteThermalMonitorRenderer(Context ctx) {
		font = ctx.getTextRenderer();
	}

	@Override
	public void render(TileEntityRemoteThermalMonitor te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		// turn the north facing layout towards the front of the block
		Direction facing = te.getFacing() == null ? Direction.NORTH : te.getFacing();
		matrixStack.translate(0.5F, 0.0F, 0.5F);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - facing.asRotation()));
		matrixStack.translate(-0.5F, 0.0F, -0.5F);

		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.translate(0.0F, -0.5F, 0.001F);

		int status = te.getStatus();
		int heat = te.getHeat();
		int level = te.getHeatLevel();
		if (status > -2) {
			float rate = 1;
			if (status > -1)
				rate = Math.round((1 - Math.min((float) heat / level, 1)) * 16) / (float) 16;

			VertexConsumer bar = buffer.getBuffer(RenderLayer.getText(TEXTURE));
			Matrix4f matrix = matrixStack.peek().getModel();
			bar.vertex(matrix, rate, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).texture(rate * BAR_U, 0).light(FULL_BRIGHT).next();
			bar.vertex(matrix, 1, 0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).texture(BAR_U, 0).light(FULL_BRIGHT).next();
			bar.vertex(matrix, 1, BAR_HEIGHT, 0).color(1.0F, 1.0F, 1.0F, 1.0F).texture(BAR_U, BAR_V).light(FULL_BRIGHT).next();
			bar.vertex(matrix, rate, BAR_HEIGHT, 0).color(1.0F, 1.0F, 1.0F, 1.0F).texture(rate * BAR_U, BAR_V).light(FULL_BRIGHT).next();
		}

		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
		matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
		matrixStack.translate(-0.5F, -0.125F, 0.0F);
		matrixStack.scale(0.015625F, 0.015625F, 0.015625F);

		String text = Integer.toString(level);
		font.draw(text, -font.getWidth(text) / 2, -font.fontHeight, 0x000000, false, matrixStack.peek().getModel(), buffer, false, 0, FULL_BRIGHT);
		matrixStack.pop();
	}
}
