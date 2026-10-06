package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HowlerAlarm extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.box(2.0D, 9.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape AABB_UP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
	protected static final VoxelShape AABB_NORTH = Block.box(2.0D, 2.0D, 9.0D, 14.0D, 14.0D, 16.0D);
	protected static final VoxelShape AABB_SOUTH = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 7.0D);
	protected static final VoxelShape AABB_WEST = Block.box(9.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape AABB_EAST = Block.box(0.0D, 2.0D, 2.0D, 7.0D, 14.0D, 14.0D);

	public HowlerAlarm() {
		super(ModItems.blockSettings());
	}

	public HowlerAlarm(Properties settings) {
		super(settings);
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityHowlerAlarm(pos, state);
	}

	@Override
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
		if (!world.isClientSide())
			world.sendBlockUpdated(pos, state, state, 2);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (world.isClientSide()) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof TileEntityHowlerAlarm)
				ScreenHandler.openHowlerAlarmScreen((TileEntityHowlerAlarm) be);
		}
		return InteractionResult.SUCCESS;
	}
}
