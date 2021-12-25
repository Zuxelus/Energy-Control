package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class TEInfoPanelExtenderRenderer implements BlockEntityRenderer<TileEntityInfoPanelExtender> {
	private static final Identifier TEXTUREOFF[];
	private static final Identifier TEXTUREON[];
	private static final CubeRenderer model[];

	static {
		TEXTUREOFF = new Identifier[16];
		TEXTUREON = new Identifier[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new Identifier(
					EnergyControl.MODID + String.format(":textures/block/info_panel/off/all%de.png", i));
			TEXTUREON[i] = new Identifier(
					EnergyControl.MODID + String.format(":textures/block/info_panel/on/all%de.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

	public TEInfoPanelExtenderRenderer(Context ctx) {}

	@Override
	public void render(TileEntityInfoPanelExtender te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
		matrixStack.push();
		int[] light = TileEntityInfoPanelRenderer.getBlockLight(te);
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		int color = 2;
		if (te.getColored()) {
			color = te.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}
		VertexConsumer vertexBuilder;
		if (te.getPowered())
			vertexBuilder = buffer.getBuffer(RenderLayer.getEntitySolid(TEXTUREON[color]));
		else
			vertexBuilder = buffer.getBuffer(RenderLayer.getEntitySolid(TEXTUREOFF[color]));
		model[te.findTexture()].render(matrixStack, vertexBuilder, light, combinedOverlay);
		matrixStack.pop();
	}

	@Override
	public int getRenderDistance() {
		return 65536;
	}
}
