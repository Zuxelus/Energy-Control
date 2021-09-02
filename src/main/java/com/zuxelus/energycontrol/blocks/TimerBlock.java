package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityTimer;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TimerBlock extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.makeCuboidShape(1.0F, 9.0F, 1.0F, 15.0F, 1.0F, 15.0F);
	protected static final VoxelShape AABB_UP = Block.makeCuboidShape(1.0F, 0.0F, 1.0F, 15.0F, 7.0F, 15.0F);
	protected static final VoxelShape AABB_NORTH = Block.makeCuboidShape(1.0F, 1.0F, 9.0F, 15.0F, 15.0F, 1.0F);
	protected static final VoxelShape AABB_SOUTH = Block.makeCuboidShape(1.0F, 1.0F, 0.0F, 15.0F, 15.0F, 7.0F);
	protected static final VoxelShape AABB_WEST = Block.makeCuboidShape(9.0F, 1.0F, 1.0F, 1.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_EAST = Block.makeCuboidShape(0.0F, 1.0F, 1.0F, 7.0F, 15.0F, 15.0F);

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityTimer();
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return 0;
		return ((TileEntityTimer) te).getPowered() ? side != state.get(FACING) ? 15 : 0 : 0;
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote)
			return ActionResultType.PASS;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityTimer))
			return ActionResultType.PASS;
		NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityTimer) te, pos);
		return ActionResultType.SUCCESS;
	}
}