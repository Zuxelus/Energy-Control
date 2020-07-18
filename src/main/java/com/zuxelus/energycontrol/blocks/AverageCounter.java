package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AverageCounter extends FacingBlock {
	public AverageCounter() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityAverageCounter te = new TileEntityAverageCounter();
		te.setFacing(meta);
		return te;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_AVERAGE_COUNTER, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory)te).dropItems(world, pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(CrossModLoader.ic2.getItemStack("machine"));
		return drops;
	}
}
