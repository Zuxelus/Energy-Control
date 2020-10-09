package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelExtenderBlockEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

public class InfoPanelExtenderBERenderer extends BlockEntityRenderer<InfoPanelExtenderBlockEntity> {
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

	public InfoPanelExtenderBERenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(InfoPanelExtenderBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		int lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().up());
		switch (be.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
			matrices.translate(0.0F, -1.0F, 0.0F);
			lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().north());
			break;
		case SOUTH:
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
			matrices.translate(0.0F, 0.0F, -1.0F);
			lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().south());
			break;
		case DOWN:
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));
			matrices.translate(0.0F, -1.0F, -1.0F);
			lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().down());
			break;
		case WEST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90));
			matrices.translate(0.0F, -1.0F, 0.0F);
			lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().west());
			break;
		case EAST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90));
			matrices.translate(-1.0F, 0.0F, 0.0F);
			lightBE = WorldRenderer.getLightmapCoordinates(be.getWorld(), be.getPos().east());
			break;
		}
		int color = 2;
		if (be.getColored()) {
			color = be.getColorBackground();
			if (color > 15 || color < 0)
				color = 2;
		}
		VertexConsumer vertexConsumer;
		if (be.getPowered())
			vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTUREON[color]));
		else
			vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTUREOFF[color]));
		model[be.findTexture()].render(matrices, vertexConsumer, lightBE, overlay);
		matrices.pop();
	}

}
