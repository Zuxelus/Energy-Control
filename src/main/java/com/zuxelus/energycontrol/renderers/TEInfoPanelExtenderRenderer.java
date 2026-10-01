package com.zuxelus.energycontrol.renderers;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
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
	private static final Identifier TEXTURE = new Identifier(EnergyControl.MODID + ":textures/block/info_panel/alle.png");
	private static final CubeRenderer model[];

	static {
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

		int color = TileEntityInfoPanelRenderer.getFaceColor(te.getColored() ? te.getColorBackground() : TileEntityInfoPanel.GREEN, te.getPowered());
		VertexConsumer vertexBuilder = buffer.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
		model[te.findTexture()].render(matrixStack, vertexBuilder, light, combinedOverlay, color);
		matrixStack.pop();
	}

	@Override
	public int getRenderDistance() {
		return 65536;
	}
}
