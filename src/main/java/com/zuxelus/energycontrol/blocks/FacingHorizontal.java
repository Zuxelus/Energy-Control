package com.zuxelus.energycontrol.blocks;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityInventory;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FacingHorizontal extends BlockHorizontal implements ITileEntityProvider, IWrenchable {

	public FacingHorizontal() {
		super(Material.IRON);
		setSoundType(SoundType.METAL);
		setHardness(6.0F);
		setCreativeTab(EnergyControl.creativeTab);
	}

	protected abstract TileEntityFacing createTileEntity(int meta);

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityFacing te = createTileEntity(meta);
		te.setFacing(meta);
		return te;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityInventory)
			((TileEntityInventory) te).dropItems(world, pos);
		super.breakBlock(world, pos, state);
	}

	protected abstract int getBlockGuiId();

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem(hand)))
			return true;
		if (!world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	// IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return ((TileEntityFacing) te).getFacing();
		return EnumFacing.NORTH;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing) {
			if (newDirection == EnumFacing.UP || newDirection == EnumFacing.DOWN)
				return true;
			((TileEntityFacing) te).setFacing(newDirection.getIndex());
			world.setBlockState(pos, getDefaultState().withProperty(FACING, newDirection));
			return true;
		}
		return false;
	}

	@Override
	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		if (!(te instanceof TileEntityInventory))
			return getDrops(world, pos, state, 1);
		List<ItemStack> list = ((TileEntityInventory) te).getDrops(fortune);
		list.add(new ItemStack(this));
		return list;
	}
}
