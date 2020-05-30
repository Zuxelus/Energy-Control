package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class InfoPanelExtender extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public InfoPanelExtender() {
		super(BlockDamages.DAMAGE_INFO_PANEL_EXTENDER, "info_panel_extender");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityInfoPanelExtender te = new TileEntityInfoPanelExtender();
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
		icons[0] = registerIcon(iconRegister,"info_panel/extender_back");
		icons[1] = registerIcon(iconRegister,"info_panel/extender_face");
		icons[2] = registerIcon(iconRegister,"info_panel/extender_side");
	}
}
