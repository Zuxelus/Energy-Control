package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class EnergyCounter extends BlockBase {
	private IIcon[] icons = new IIcon[2];

	public EnergyCounter() {
		super(BlockDamages.DAMAGE_ENERGY_COUNTER, "energy_counter");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityEnergyCounter te = new TileEntityEnergyCounter();
		te.setFacing(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		return side == 1 ? icons[1] : icons[0];
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"energy_counter/input");
		icons[1] = registerIcon(iconRegister,"energy_counter/output");
	}
}