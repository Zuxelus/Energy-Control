package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TimerBlock extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.createCuboidShape(1.0F, 9.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_UP = Block.createCuboidShape(1.0F, 0.0F, 1.0F, 15.0F, 7.0F, 15.0F);
	protected static final VoxelShape AABB_NORTH = Block.createCuboidShape(1.0F, 1.0F, 9.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_SOUTH = Block.createCuboidShape(1.0F, 1.0F, 0.0F, 15.0F, 15.0F, 7.0F);
	protected static final VoxelShape AABB_WEST = Block.createCuboidShape(9.0F, 1.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_EAST = Block.createCuboidShape(0.0F, 1.0F, 1.0F, 7.0F, 15.0F, 15.0F);

	public TimerBlock() {
		super(FabricBlockSettings.copyOf(ModItems.settings));
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.timer.instantiate(pos, state);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView blockAccess, BlockPos pos, Direction side) {
		BlockEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return 0;
		if (side == state.get(FACING) || side == ((TileEntityTimer) te).getRotation().getOpposite())
			return 0;
		return ((TileEntityTimer) te).getPowered() ? 15 : 0;
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return ActionResult.PASS;
		player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	@Override
	public void neighborUpdate(BlockState state, World level, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
		if (!level.isClient) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof TileEntityTimer)
				((TileEntityTimer) be).onNeighborChange(fromBlock, fromPos);
		}
	}
}