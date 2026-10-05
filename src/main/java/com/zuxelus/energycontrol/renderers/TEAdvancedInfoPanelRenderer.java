package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

public class TEAdvancedInfoPanelRenderer implements BlockEntityRenderer<TileEntityAdvancedInfoPanel> {
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

	public TEAdvancedInfoPanelRenderer(Context ctx) {
		font = ctx.getTextRenderer();
	}

	@Override
	// the panel body is a baked model (PanelModel), only the text is drawn here
	public void render(TileEntityAdvancedInfoPanel te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, int combinedOverlay) {
		Screen screen = te.getScreen();
		if (!te.powered || screen == null)
			return;
		matrixStack.push();
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

		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		RotationOffset offset = new RotationOffset(thickness * 2, te.rotateHor / 7, te.rotateVert / 7);
		List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
		if (joinedData != null)
			drawText(te, joinedData, matrixStack, buffer, combinedLight, thickness, offset);
		matrixStack.pop();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, MatrixStack matrixStack, VertexConsumerProvider buffer, int combinedLight, byte thickness, RotationOffset offset) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getPos();
		float displayWidth = 1.0F;
		float displayHeight = 1.0F;
		float dx = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dx = screen.minX - pos.getX();
					dz = pos.getZ() - screen.maxZ;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = pos.getZ() - screen.maxZ - screen.minZ + pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dx = screen.minX - pos.getX();
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case DOWN:
					break;
				case UP:
					break;
				}
				break;
			case DOWN:
				switch (panel.getRotation()) {
				case NORTH:
					dx = pos.getX() - screen.maxX;
					dz = pos.getZ() - screen.maxZ;
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dx = screen.minX - pos.getX();
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dx = pos.getX() - screen.maxX;
					dz = screen.minZ - pos.getZ();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dx = screen.minX - pos.getX();
					dz = pos.getZ() - screen.maxZ;
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
				dx = pos.getX() - screen.maxX;
				dz = screen.minY - pos.getY();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dx = screen.minX - pos.getX();
				dz = screen.minY - pos.getY();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case WEST:
				dz = screen.minZ - pos.getZ();
				dx = screen.minY - pos.getY();
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dz = pos.getZ() - screen.maxZ;
				dx = screen.minY - pos.getY();
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
		switch(panel.getRotation())
		{
		case UP:
			break;
		case NORTH:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
			matrixStack.translate(dx - 1.0F, dz, 0.0F);
			break;
		case SOUTH:
			matrixStack.translate(dx, dz - 1.0F, 0.0F);
			break;
		case DOWN:
			break;
		case WEST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90));
			matrixStack.translate(dz, dx, 0.0F);
			break;
		case EAST:
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
			matrixStack.translate(dz - 1.0F, dx - 1.0F, 0.0F);
			break;
		}

		double h = (offset.leftBottom - offset.rightBottom) / 32;
		double v = (offset.leftTop - offset.leftBottom) / 32;
		double b = Math.atan(h / displayWidth);
		double a = Math.atan(Math.cos(b) * v / displayHeight);
		int i = offset.rotateVert == 0 ? 0 : offset.rotateVert > 0 ? -1 : 1;
		int j = offset.rotateHor == 0 ? 0 : offset.rotateHor > 0 ? -1 : 1;
		matrixStack.translate(displayWidth / 2, displayHeight / 2, 1 + (32 * h - offset.leftTop - offset.leftBottom) / 64);
		matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((float) Math.toDegrees(b)));
		matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion((float) Math.toDegrees(a)));
		matrixStack.multiply(new Vec3f(0.0F, 0.0F, i * j).getDegreesQuaternion(90.0F - (float) Math.toDegrees( // Law of cosines
			Math.acos((h * h + v * v) / 2 / Math.sqrt(displayWidth * displayWidth + h * h) / Math.sqrt(displayHeight * displayHeight + v * v)))));
		matrixStack.translate(0.0F, 0.001F * i, 0.001F);
		displayHeight = (float) ((displayHeight - 0.125F) / Math.cos(a));
		displayWidth = (float) ((displayWidth - 0.125F) / Math.cos(b));

		// getMaxWidth
		int maxWidth = 1;
		for (PanelString panelString : joinedData) {
			String currentString = implodeArray(new String[] { panelString.textLeft, panelString.textCenter, panelString.textRight }, " ");
			maxWidth = Math.max(font.getWidth(currentString), maxWidth);
		}
		maxWidth += 4;

		int lineHeight = font.fontHeight + 2;
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

		//matrixStack.disableLighting();

		int row = 0;
		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorTextHex();
		for (PanelString panelString : joinedData) {
			if (panelString.textLeft != null) {
				font.draw(panelString.textLeft, offsetX - realWidth / 2,
						1 + offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorLeft != 0 ? panelString.colorLeft : colorHex, false, matrixStack.peek().getModel(), buffer, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			}
			if (panelString.textCenter != null) {
				font.draw(panelString.textCenter,
						-font.getWidth(panelString.textCenter) / 2,
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorCenter != 0 ? panelString.colorCenter : colorHex, false, matrixStack.peek().getModel(), buffer, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			}
			if (panelString.textRight != null) {
				font.draw(panelString.textRight,
						realWidth / 2 - font.getWidth(panelString.textRight),
						offsetY - realHeight / 2 + row * lineHeight,
						panelString.colorRight != 0 ? panelString.colorRight : colorHex, false, matrixStack.peek().getModel(), buffer, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
			}
			row++;
		}

		//matrixStack.enableLighting();
		//matrixStack.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	// a screen can be larger than the core block: keep drawing the text while only other parts of it are in view
	@Override
	public boolean rendersOutsideBoundingBox(TileEntityAdvancedInfoPanel te) {
		return true;
	}

	@Override
	public int getRenderDistance() {
		return 65536;
	}
}
