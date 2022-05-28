package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class HowlerAlarm extends FacingBlockSmall {
	protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.125D, 0.5625D, 0.125D, 0.875D, 1.0D, 0.875D);
	protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.4375D, 0.875D);
	protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.125D, 0.125D, 0.5625D, 0.875D, 0.875D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.125D, 0.125D, 0.0D, 0.875D, 0.875D, 0.4375D);
	protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.5625D, 0.125D, 0.125D, 1.0D, 0.875D, 0.875D);
	protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.125D, 0.125D, 0.4375D, 0.875D, 0.875D);

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityHowlerAlarm();
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (checkForDrop(world, pos, state) && !canPlaceBlock(world, pos, state.getValue(FACING).getOpposite())) {
			dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
		} else 
			if (!world.isRemote)
				world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(FACING)) {
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
		if (CrossModLoader.getCrossMod(ModIDs.IC2).isWrench(player.getHeldItem(hand)))
			return true;
		if (world.isRemote)
			player.openGui(EnergyControl.instance, getBlockGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_HOWLER_ALARM;
	}
}
