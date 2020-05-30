package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class InfoPanel extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public InfoPanel() {
		super(BlockDamages.DAMAGE_INFO_PANEL, "info_panel");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityInfoPanel te = new TileEntityInfoPanel();
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
		icons[0] = registerIcon(iconRegister,"info_panel/panel_back");
		icons[1] = registerIcon(iconRegister,"info_panel/panel_face");
		icons[2] = registerIcon(iconRegister,"info_panel/panel_side");
	}
}