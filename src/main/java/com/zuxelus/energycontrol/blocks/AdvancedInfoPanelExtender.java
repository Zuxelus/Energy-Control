package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.Screen;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanelExtender;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class AdvancedInfoPanelExtender  extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public AdvancedInfoPanelExtender() {
		super(BlockDamages.DAMAGE_ADVANCED_EXTENDER, "info_panel_extender_advanced");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityAdvancedInfoPanelExtender te = new TileEntityAdvancedInfoPanelExtender();
		te.setFacing(0);
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
		icons[0] = registerIcon(iconRegister,"info_panel/extender_advanced_back");
		icons[1] = registerIcon(iconRegister,"info_panel/extender_advanced_face");
		icons[2] = registerIcon(iconRegister,"info_panel/extender_advanced_side");
	}

	@Override
	public float[] getBlockBounds(TileEntity tile) {
		float[] bounds = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		if (!(tile instanceof TileEntityAdvancedInfoPanelExtender))
			return bounds;

		TileEntityAdvancedInfoPanelExtender te = (TileEntityAdvancedInfoPanelExtender) tile;
		Screen screen = te.getScreen();
		if (screen == null)
			return bounds;

		switch (te.getFacingForge()) {
		case EAST:
			return new float[] { 0.0F, 0.0F, 0.0F, 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0F, 1.0F };
		case WEST:
			return new float[] { 1.0F - 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0F, 0.0F, 1.0F, 1.0F, 1.0F };
		case SOUTH:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness() };
		case NORTH:
			return new float[] { 0.0F, 0.0F, 1.0F - 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0F, 1.0F, 1.0F };
		case UP:
			return new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 1.0F };
		case DOWN:
			return new float[] { 0.0F, 1.0F - 0.0625F * ((TileEntityAdvancedInfoPanelExtender)te).getThickness(), 0.0F, 1.0F, 1.0F, 1.0F };
		default:
			return bounds;
		}
	}
}
