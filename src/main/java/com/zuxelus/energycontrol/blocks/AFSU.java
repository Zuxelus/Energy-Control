package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class AFSU extends FacingBlock {

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityAFSU te = new TileEntityAFSU();
		te.setFacing(meta);
		return te;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_AFSU, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
		if (!world.isRemote)
			world.notifyBlockUpdate(pos, state, state, 2);
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
		drops.add(CrossModLoader.ic2.getItemStack("mfsu"));
		return drops;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityAFSU))
			return 0;
		return ((TileEntityAFSU) te).getPowered() ? 15 : 0;
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
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
