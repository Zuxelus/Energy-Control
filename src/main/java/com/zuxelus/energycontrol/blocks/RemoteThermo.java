package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class RemoteThermo extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public RemoteThermo() {
		super(BlockDamages.DAMAGE_REMOTE_THERMO, "remote_thermo");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityRemoteThermo te = new TileEntityRemoteThermo();
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
		icons[0] = registerIcon(iconRegister,"remote_thermo/back");
		icons[1] = registerIcon(iconRegister,"remote_thermo/face");
		icons[2] = registerIcon(iconRegister,"remote_thermo/side");
	}
}
