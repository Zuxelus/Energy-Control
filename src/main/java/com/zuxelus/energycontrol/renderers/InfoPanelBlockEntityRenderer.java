package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;
import com.zuxelus.energycontrol.blockentities.Screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class InfoPanelBlockEntityRenderer extends BlockEntityRenderer<InfoPanelBlockEntity> {
	private static final Identifier TEXTUREOFF[];
	private static final Identifier TEXTUREON[];
	private static final CubeRenderer model[];

	static {
		TEXTUREOFF = new Identifier[16];
		TEXTUREON = new Identifier[16];
		for (int i = 0; i < 16; i++) {
			TEXTUREOFF[i] = new Identifier(
					EnergyControl.MODID + String.format(":textures/block/info_panel/off/all%d.png", i));
			TEXTUREON[i] = new Identifier(
					EnergyControl.MODID + String.format(":textures/block/info_panel/on/all%d.png", i));
		}
		model = new CubeRenderer[16];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				model[i * 4 + j] = new CubeRenderer(i * 32 + 64, j * 32 + 64);
	}

	public InfoPanelBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	private static String implodeArray(String[] inputArray, String glueString) {
		String output = "";
		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i] == null || inputArray[i].isEmpty())
					continue;
				sb.append(glueString);
				sb.append(inputArray[i]);
			}
			output = sb.toString();
			if (output.length() > 1)
				output = output.substring(1);
		}
		return output;
	}

	@Override
	public void render(InfoPanelBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
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
		if (be.getPowered()) {
			List<PanelString> joinedData = be.getPanelStringList(be.getShowLabels());
			if (joinedData != null) {
				drawText(be, joinedData, matrices, vertexConsumers, light);
			}
		}
		matrices.pop();
	}

	private void drawText(InfoPanelBlockEntity panel, List<PanelString> joinedData, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getPos();
		float displayWidth = 1 - 2F / 16;
		float displayHeight = 1 - 2F / 16;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = (pos.getZ() - screen.maxZ - screen.minZ + pos.getZ());
					dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case DOWN:
					break;
				case UP:
					break;
				}
				break;
			case NORTH:
				dz = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				dy = pos.getX() - screen.maxX - screen.minX + pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ + pos.getZ() - screen.minZ;
				dy = - (pos.getY() - screen.maxY - screen.minY + pos.getY());
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrices.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
		
		switch (panel.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90));
			break;
		case EAST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90));
			break;
		}
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90));
			break;
		case EAST:
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90));
			break;
		}

		if (panel.isTouchCard()) {
			matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
			panel.renderImage(dispatcher.textureManager, matrices.peek().getModel());
		} else 
			renderText(panel, joinedData, displayWidth, displayHeight, matrices, vertexConsumers, light);
	}

	private void renderText(InfoPanelBlockEntity panel, List<PanelString> joinedData, float displayWidth, float displayHeight, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		TextRenderer fontRenderer = dispatcher.getTextRenderer();
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(fontRenderer.getWidth(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = fontRenderer.fontHeight + 2;
		int requiredHeight = lineHeight * joinedData.size();
		float scaleX = displayWidth / maxWidth;
		float scaleY = displayHeight / requiredHeight;
		float scale = Math.min(scaleX, scaleY);
		matrices.scale(scale, -scale, scale);
		int realHeight = (int) Math.floor(displayHeight / scale);
		int realWidth = (int) Math.floor(displayWidth / scale);
		int offsetX;
		int offsetY;
		if (scaleX < scaleY) {
			offsetX = 2;
			offsetY = (realHeight - requiredHeight) / 2;
		} else {
			offsetX = (realWidth - maxWidth) / 2 + 2;
			offsetY = 0;
		}

		//RenderSystem.disableLighting();

		int row = 0;
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorTextHex();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
				fontRenderer.draw(panelString.textLeft, offsetX - realWidth / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorLeft != 0 ? panelString.colorLeft : colorHex, false, matrices.peek().getModel(), vertexConsumers, false, 0, light);
			}
			if (panelString.textCenter != null) {
				fontRenderer.draw(panelString.textCenter,
						-fontRenderer.getWidth(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorCenter != 0 ? panelString.colorCenter : colorHex, false, matrices.peek().getModel(), vertexConsumers, false, 0, light);
			}
			if (panelString.textRight != null) {
				fontRenderer.draw(panelString.textRight,
						realWidth / 2 - fontRenderer.getWidth(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorRight != 0 ? panelString.colorRight : colorHex, false, matrices.peek().getModel(), vertexConsumers, false, 0, light);
			}
			row++;
		}

		//RenderSystem.enableLighting();
		//RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
