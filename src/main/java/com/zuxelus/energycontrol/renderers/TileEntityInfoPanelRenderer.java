package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class TileEntityInfoPanelRenderer implements BlockEntityRenderer<TileEntityInfoPanel> {
	private static int[][] sides = new int[][] { { 3, 2, 1, 0, 5, 4 }, { 2, 3, 1, 0, 4, 5 }, { 4, 5, 1, 0, 3, 2 },
		{ 5 ,4, 1, 0, 2, 3 }, { 1, 0, 3, 2, 4, 5 }, { 0, 1, 2, 3, 4, 5 } };
	private final TextRenderer font;

	private static String implodeArray(String[] inputArray, String glueString) {
		String output = "";
		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (String s : inputArray) {
				if (s == null || s.isEmpty())
					continue;
				sb.append(glueString);
				sb.append(s);
			}
			output = sb.toString();
			if (output.length() > 1)
				output = output.substring(1);
		}
		return output;
	}

	public static int[] getBlockLight(BlockEntityFacing te) {
		int[] light = new int[6];
		light[sides[te.getFacing().getId()][0]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.DOWN));
		light[sides[te.getFacing().getId()][1]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.UP));
		light[sides[te.getFacing().getId()][2]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.WEST));
		light[sides[te.getFacing().getId()][3]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.EAST));
		light[sides[te.getFacing().getId()][4]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.NORTH));
		light[sides[te.getFacing().getId()][5]] = WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getPos().offset(Direction.SOUTH));
		return light;
	}

	public TileEntityInfoPanelRenderer(Context ctx) {
		font = ctx.getTextRenderer();
	}

	@Override
	// the panel body is a baked model (PanelModel), only the text is drawn here
	public void render(TileEntityInfoPanel te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
		if (!te.getPowered())
			return;
		matrixStack.push();
		switch (te.getFacing()) {
		case UP:
			break;
		case NORTH:
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case SOUTH:
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
			matrixStack.translate(0.0F, 0.0F, -1.0F);
			break;
		case DOWN:
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
			matrixStack.translate(0.0F, -1.0F, -1.0F);
			break;
		case WEST:
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
			matrixStack.translate(0.0F, -1.0F, 0.0F);
			break;
		case EAST:
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90));
			matrixStack.translate(-1.0F, 0.0F, 0.0F);
			break;
		}

		List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
		drawText(te, joinedData, matrixStack, buffer, combinedLight);
		matrixStack.pop();
	}

	private void drawText(TileEntityInfoPanel panel, List<PanelString> joinedData, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight) {
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

		matrixStack.translate(0.5F - dy / 2, 1.01F - dx / 2 , 0.5F - dz / 2);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
			break;
		case SOUTH:
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90));
			break;
		case EAST:
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
			break;
		}

		if (panel.isTouchCard() || panel.hasBars()) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
			panel.renderImage(displayWidth, displayHeight, matrixStack);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
		}
		if (joinedData != null) {
			matrixStack.translate(0, 0, 0.0002F);
			int colorHex = 0x000000;
			if (panel.getColored())
				colorHex = panel.getColorTextHex();
			renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, font);
		}
	}

	public static void renderText(List<PanelString> joinedData, float displayWidth, float displayHeight, int colorHex, MatrixStack matrixStack, TextRenderer fontRenderer) {
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
		matrixStack.scale(scale, -scale, scale);
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

		int row = 0;
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null)
				drawString(fontRenderer, matrixStack, panelString.textLeft, offsetX - realWidth / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorLeft != 0 ? panelString.colorLeft : colorHex);
			if (panelString.textCenter != null)
				drawString(fontRenderer, matrixStack, panelString.textCenter, -fontRenderer.getWidth(panelString.textCenter) / 2,
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorCenter != 0 ? panelString.colorCenter : colorHex);
			if (panelString.textRight != null)
				drawString(fontRenderer, matrixStack, panelString.textRight, realWidth / 2 - fontRenderer.getWidth(panelString.textRight),
					offsetY - realHeight / 2 + row * lineHeight, panelString.colorRight != 0 ? panelString.colorRight : colorHex);
			row++;
		}
	}

	// replacement for TextRenderer.draw(MatrixStack, ...) removed in 1.20: immediate full-bright draw; the polygon offset keeps the text off the screen face
	private static void drawString(TextRenderer fontRenderer, MatrixStack matrixStack, String text, float x, float y, int color) {
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		fontRenderer.draw(text, x, y, color, false, matrixStack.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.POLYGON_OFFSET, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		immediate.draw();
	}

	// a screen can be larger than the core block: keep drawing the text while only other parts of it are in view
	@Override
	public boolean rendersOutsideBoundingBox(TileEntityInfoPanel te) {
		return true;
	}

	@Override
	public int getRenderDistance() {
		return 65536;
	}
}
