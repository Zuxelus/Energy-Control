package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class AverageCounter extends BlockBase {
	private IIcon[] icons = new IIcon[2];

	public AverageCounter() {
		super(BlockDamages.DAMAGE_AVERAGE_COUNTER, "average_counter");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityAverageCounter te = new TileEntityAverageCounter();
		te.setFacing(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		return side == 1 ? icons[1] : icons[0];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"average_counter/input");
		icons[1] = registerIcon(iconRegister,"average_counter/output");
	}
}
