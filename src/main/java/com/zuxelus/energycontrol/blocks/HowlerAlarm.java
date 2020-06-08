package com.zuxelus.energycontrol.blocks;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HowlerAlarm extends FacingBlock implements ITileEntityProvider, IWrenchable {
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.125D, 0.5625D, 0.125D, 0.875D, 1.0D, 0.875D);
	protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.4375D, 0.875D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.125D, 0.125D, 0.5625D, 0.875D, 0.875D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.4375D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.4375D, 0.875D, 0.875D);

	public HowlerAlarm() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityHowlerAlarm te = new TileEntityHowlerAlarm();
		te.setFacing(meta);
		return te;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return canPlaceBlock(worldIn, pos, side.getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if (canPlaceBlock(worldIn, pos, enumfacing))
				return true;
		}
		return false;
	}

	protected static boolean canPlaceBlock(World worldIn, BlockPos pos, EnumFacing direction) {
		BlockPos blockpos = pos.offset(direction);
		return worldIn.getBlockState(blockpos).isSideSolid(worldIn, blockpos, direction.getOpposite());
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return canPlaceBlock(worldIn, pos, facing.getOpposite()) ? this.getDefaultState().withProperty(FACING, facing) : this.getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (this.checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, ((EnumFacing) state.getValue(FACING)).getOpposite())) {
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		} else 
			if (!world.isRemote)
				world.notifyBlockUpdate(pos, state, state, 2);
	}

	private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
		if (this.canPlaceBlockAt(worldIn, pos))
			return true;
		this.dropBlockAsItem(worldIn, pos, state, 0);
		worldIn.setBlockToAir(pos);
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

		switch (enumfacing) {
		case EAST:
			return AABB_EAST;
		case WEST:
			return AABB_WEST;
		case SOUTH:
			return AABB_SOUTH;
		case NORTH:
		default:
			return AABB_NORTH;
		case UP:
			return AABB_UP;
		case DOWN:
			return AABB_DOWN;
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (CrossModLoader.ic2.isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_HOWLER_ALARM, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	//IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return ((TileEntityFacing) te).getFacing();
		return EnumFacing.DOWN;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return getDrops(world, pos, state, 1);
	}
}
