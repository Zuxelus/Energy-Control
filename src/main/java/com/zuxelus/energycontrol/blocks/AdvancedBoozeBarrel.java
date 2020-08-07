package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.TileEntityBoozeBarrel;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class AdvancedBoozeBarrel extends BlockHorizontal implements ITileEntityProvider {

	public AdvancedBoozeBarrel() {
		super(Material.IRON);
		setHardness(6.0F);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityBoozeBarrel te = new TileEntityBoozeBarrel();
		te.setFacing(meta);
		return te;
	}
}
