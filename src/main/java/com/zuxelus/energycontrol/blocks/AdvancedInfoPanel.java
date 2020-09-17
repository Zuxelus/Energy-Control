package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class AdvancedInfoPanel extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public AdvancedInfoPanel() {
		super(BlockDamages.DAMAGE_ADVANCED_PANEL, "info_panel_advanced");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityAdvancedInfoPanel te = new TileEntityAdvancedInfoPanel();
		te.setFacing(0);
		te.setRotation(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		if (side > 1)
			return icons[2];
		return icons[side];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"info_panel/panel_advanced_back");
		icons[1] = registerIcon(iconRegister,"info_panel/panel_advanced_face");
		icons[2] = registerIcon(iconRegister,"info_panel/panel_advanced_side");
	}

	@Override
	public float[] getBlockBounds(TileEntity tile) {
		float[] bounds = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return bounds;

		TileEntityAdvancedInfoPanel te = (TileEntityAdvancedInfoPanel) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return bounds;

		switch (te.getFacingForge()) {
		case EAST:
			return new float[] { 0.0F, 0.0F, 0.0F, 0.0625F * te.thickness, 1.0F, 1.0F };
		case WEST:
			return new float[] { 1.0F - 0.0625F * te.thickness, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		case SOUTH:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F * te.thickness };
		case NORTH:
			return new float[] { 0.0F, 0.0F, 1.0F - 0.0625F * te.thickness, 1.0F, 1.0F, 1.0F };
		case UP:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 0.0625F * te.thickness, 1.0F };
		case DOWN:
			return new float[] { 0.0F, 1.0F - 0.0625F * te.thickness, 0.0F, 1.0F, 1.0F, 1.0F };
		default:
			return bounds;
		}
	}
}
