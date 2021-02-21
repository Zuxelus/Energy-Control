package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class HowlerAlarm extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.makeCuboidShape(2.0D, 9.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape AABB_UP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
	protected static final VoxelShape AABB_NORTH = Block.makeCuboidShape(2.0D, 2.0D, 9.0D, 14.0D, 14.0D, 16.0D);
	protected static final VoxelShape AABB_SOUTH = Block.makeCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 7.0D);
	protected static final VoxelShape AABB_WEST = Block.makeCuboidShape(9.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape AABB_EAST = Block.makeCuboidShape(0.0D, 2.0D, 2.0D, 7.0D, 14.0D, 14.0D);

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.howler_alarm.get().create();
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isRemote)
			world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		switch (state.get(FACING)) {
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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityHowlerAlarm)
				ScreenHandler.openHowlerAlarmScreen((TileEntityHowlerAlarm) te);
		}
		return ActionResultType.SUCCESS;
	}
}
