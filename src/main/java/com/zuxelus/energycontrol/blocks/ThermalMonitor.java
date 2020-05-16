package com.zuxelus.energycontrol.blocks;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.IRedstoneConsumer;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityThermo;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ThermalMonitor extends FacingBlock implements ITileEntityProvider, IWrenchable {
    protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.0625F, 0.5625D, 0.0625F, 0.9375F, 1.0D, 0.9375F); // 1 9 1 15 16 15
    protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.0625F, 0.0D, 0.0625F, 0.9375F, 0.4375D, 0.9375F);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.0625F, 0.0625F, 0.5625D, 0.9375F, 0.9375F, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.0625F, 0.0625F, 0.4375D, 0.9375F, 0.9375F, 0.0D);
    protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.0625F, 0.0625F, 1.0D, 0.9375F, 0.9375F);
    protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.0625F, 0.0625F, 0.4375D, 0.9375F, 0.9375F);
    EnumFacing rotation;
    private boolean powered = false;

	public ThermalMonitor() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityThermo te = new TileEntityThermo();
		if (rotation != null)
			te.setRotation(rotation.getIndex());
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
		switch (facing) {
		case UP:
		case DOWN:
			rotation = placer.getHorizontalFacing().getOpposite();
			break;
		case NORTH:
		case SOUTH:
		case EAST:
		case WEST:
			rotation = EnumFacing.DOWN;
			break;
		}
		return canPlaceBlock(worldIn, pos, facing.getOpposite()) ? getDefaultState().withProperty(FACING, facing) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, ((EnumFacing) state.getValue(FACING)).getOpposite())) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		}
	}

	private boolean checkForDrop(World world, BlockPos pos, IBlockState state) {
		if (canPlaceBlockAt(world, pos))
			return true;
		dropBlockAsItem(world, pos, state, 0);
		world.setBlockToAir(pos);
		return false;
	}
	
	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return powered ? side != blockState.getValue(FACING) ? 15 : 0 : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return powered ? side != blockState.getValue(FACING) ? 15 : 0 : 0;
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
			player.openGui(EnergyControl.instance, BlockDamages.DAMAGE_THERMAL_MONITOR, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	public void setPowered(boolean value) {
		powered = value; 
	}

	// IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing)
			return ((TileEntityFacing) te).getFacing();
		return EnumFacing.DOWN;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityFacing && newDirection != ((TileEntityFacing) te).getRotation()) {
			((TileEntityFacing) te).setRotation(newDirection.getIndex());
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
		return getDrops(world, pos, state, 1);
	}
}
