package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HowlerAlarm extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.createCuboidShape(2.0D, 9.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape AABB_UP = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
	protected static final VoxelShape AABB_NORTH = Block.createCuboidShape(2.0D, 2.0D, 9.0D, 14.0D, 14.0D, 16.0D);
	protected static final VoxelShape AABB_SOUTH = Block.createCuboidShape(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 7.0D);
	protected static final VoxelShape AABB_WEST = Block.createCuboidShape(9.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
	protected static final VoxelShape AABB_EAST = Block.createCuboidShape(0.0D, 2.0D, 2.0D, 7.0D, 14.0D, 14.0D);

	public HowlerAlarm() {
		super(FabricBlockSettings.copyOf(ModItems.settings));
	}

	public HowlerAlarm(Settings settings) {
		super(settings);
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityHowlerAlarm(pos, state);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isClient)
			world.updateListeners(pos, state, state, 2);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof TileEntityHowlerAlarm)
				ScreenHandler.openHowlerAlarmScreen((TileEntityHowlerAlarm) be);
		}
		return ActionResult.SUCCESS;
	}
}
