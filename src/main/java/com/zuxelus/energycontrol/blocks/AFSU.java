package com.zuxelus.energycontrol.blocks;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AFSU extends FacingBlock {

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityAFSU();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_AFSU;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null)
			return;
		double energy = tag.getDouble("energy");
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityAFSU) || energy == 0)
			return;
		((TileEntityAFSU) te).setEnergy(energy);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!world.isRemote)
			world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.ic2.getItemStack("mfsu"));
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityAFSU))
			return 0;
		return ((TileEntityAFSU) te).getPowered() ? 15 : 0;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(getStackwithEnergy(0));
		items.add(getStackwithEnergy(TileEntityAFSU.CAPACITY));
	}

	private ItemStack getStackwithEnergy(double energy) {
		ItemStack stack = new ItemStack(this);
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble("energy", energy);
		return stack;
	}

	//IWrenchable
	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		if (!(te instanceof TileEntityAFSU))
			return Collections.emptyList();
		List<ItemStack> list = ((TileEntityInventory) te).getDrops(fortune);
		list.add(getStackwithEnergy(((TileEntityAFSU) te).getEnergy() * 0.8D));
		return list;
	}
}
