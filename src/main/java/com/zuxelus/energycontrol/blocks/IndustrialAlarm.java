package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class IndustrialAlarm extends HowlerAlarm {
	public IndustrialAlarm() {
		super();
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityIndustrialAlarm te = new TileEntityIndustrialAlarm();
		te.setFacing(meta);
		return te;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityIndustrialAlarm))
			return 0;
		return ((TileEntityIndustrialAlarm)te).lightLevel;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_INDUSTRIAL_ALARM, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}
