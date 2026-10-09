package com.zuxelus.energycontrol.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class TEAdvancedInfoPanelRenderer extends TileRenderer<TileEntityAdvancedInfoPanel> {
	private final Font font;

	public TEAdvancedInfoPanelRenderer(Context ctx) {
		font = ctx.font();
	}

	@Override
	public AABB getRenderBoundingBox(TileEntityAdvancedInfoPanel te) {
		return te.getRenderBoundingBox();
	}

	@Override
	protected void render(TileEntityAdvancedInfoPanel te, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, int combinedOverlay) {
		matrixStack.pushPose();
		// the panel body is a baked model (PanelModel), only the text is drawn here
		PanelCube.rotateBlock(matrixStack, te.getFacing(), te.getRotation());
		byte thickness = te.thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		RotationOffset offset = new RotationOffset(thickness * 2, te.rotateHor / 7, te.rotateVert / 7);
		Screen screen = te.getScreen();

		if (screen != null) {
			PanelCube.rotateBlockText(matrixStack, te.getFacing(), te.getRotation());

			if (te.getPowered()) {
				List<PanelString> joinedData = te.getPanelStringList(false, te.getShowLabels());
				if (joinedData != null)
					drawText(te, joinedData, matrixStack, buffer, combinedLight, thickness, offset);
			}
		}
		matrixStack.popPose();
	}

	private void drawText(TileEntityAdvancedInfoPanel panel, List<PanelString> joinedData, PoseStack matrixStack, SubmitNodeCollector buffer, int combinedLight, byte thickness, RotationOffset offset) {
		Screen screen = panel.getScreen();
		BlockPos pos = panel.getBlockPos();
		float displayWidth = 1.0F;
		float displayHeight = 1.0F;
		float dx = 0; float dy = 0; float dz = 0;
		if (screen != null) {
			switch (panel.getFacing()) {
			case UP:
				switch (panel.getRotation()) {
				case NORTH:
					dz = screen.minZ - pos.getZ();
					dy = screen.maxX - pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case SOUTH:
					dz = screen.maxZ - pos.getZ();
					dy = screen.minX - pos.getX();
					displayWidth += screen.maxX - screen.minX;
					displayHeight += screen.maxZ - screen.minZ;
					break;
				case EAST:
					dz = screen.maxZ - pos.getZ();
					dy = screen.maxX - pos.getX();
					displayWidth += screen.maxZ - screen.minZ;
					displayHeight += screen.maxX - screen.minX;
					break;
				case WEST:
					dz = screen.minZ - pos.getZ();
					dy = screen.minX - pos.getX();
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
				dz = pos.getY() - screen.minY;
				dy = pos.getX() - screen.maxX;
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case SOUTH:
				dz = pos.getY() - screen.minY;
				dy = screen.minX - pos.getX();
				displayWidth += screen.maxX - screen.minX;
				displayHeight += screen.maxY - screen.minY;
				break;
			case DOWN:
 				break;
			case WEST:
				dy = screen.minZ - pos.getZ();
				dz = pos.getY() - screen.minY;
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			case EAST:
				dy = pos.getZ() - screen.maxZ;
				dz = pos.getY() - screen.minY;
				displayWidth += screen.maxZ - screen.minZ;
				displayHeight += screen.maxY - screen.minY;
				break;
			}
		}

		matrixStack.translate(dy, dx, dz);
		matrixStack.rotate(Axis.XP.rotationDegrees(-90));
		if (panel.getFacing() == Direction.UP) {
			switch(panel.getRotation()) {
			case UP:
				break;
			case NORTH:
				matrixStack.rotate(Axis.ZP.rotationDegrees(180));
				matrixStack.translate(-1.0F, -1.0F, 0.0F);
				break;
			case SOUTH:
				matrixStack.translate(0.0F, 0.0F, 0.0F);
				break;
			case DOWN:
				break;
			case WEST:
				matrixStack.rotate(Axis.ZP.rotationDegrees(-90));
				matrixStack.translate(-1.0F, 0.0F, 0.0F);
				break;
			case EAST:
				matrixStack.rotate(Axis.ZP.rotationDegrees(90));
				matrixStack.translate(0.0F, -1.0F, 0.0F);
				break;
			}
		}

		double h = (offset.leftBottom - offset.rightBottom) / 32;
		double v = (offset.leftTop - offset.leftBottom) / 32;
		double b = Math.atan(h / displayWidth);
		double a = Math.atan(Math.cos(b) * v / displayHeight);
		int i = offset.rotateVert == 0 ? 0 : offset.rotateVert > 0 ? -1 : 1;
		int j = offset.rotateHor == 0 ? 0 : offset.rotateHor > 0 ? -1 : 1;
		matrixStack.translate(displayWidth / 2, displayHeight / 2, 1 + (32 * h - offset.leftTop - offset.leftBottom) / 64);
		matrixStack.rotate(Axis.YN.rotationDegrees((float) Math.toDegrees(b)));
		matrixStack.rotate(Axis.XN.rotationDegrees((float) Math.toDegrees(a)));
		/*matrixStack.mulPose(new Vector3f(0.0F, 0.0F, i * j).rotationDegrees(90.0F - (float) Math.toDegrees( // Law of cosines
			Math.acos((h * h + v * v) / 2 / Math.sqrt(displayWidth * displayWidth + h * h) / Math.sqrt(displayHeight * displayHeight + v * v)))));*/
		matrixStack.translate(0.0F, 0.001F * i, 0.001F);
		displayHeight = (float) ((displayHeight - 0.125F) / Math.cos(a));
		displayWidth = (float) ((displayWidth - 0.125F) / Math.cos(b));

		int colorHex = 0x000000;
		if (panel.getColored())
			colorHex = panel.getColorText();
		TileEntityInfoPanelRenderer.renderText(joinedData, displayWidth, displayHeight, colorHex, matrixStack, buffer, font);
	}

}