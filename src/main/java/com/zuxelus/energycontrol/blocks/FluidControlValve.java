package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityFluidControlValve;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FluidControlValve extends FacingHorizontalActive {
	protected static final VoxelShape AABB_NORTH = Block.box(0.0F, 4.0F, 4.0F, 16.0F, 12.0F, 12.0F);
	protected static final VoxelShape AABB_WEST = Block.box(4.0F, 4.0F, 0.0F, 12.0F, 12.0F, 16.0F);

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityFluidControlValve();
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide)
			world.sendBlockUpdated(pos, state, state, 2);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(FACING)) {
		case EAST:
		case WEST:
			return AABB_WEST;
		case SOUTH:
		case NORTH:
		default:
			return AABB_NORTH;
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityFluidControlValve))
			return ActionResultType.PASS;
		if (!world.isClientSide)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityFluidControlValve) te, pos);
		return ActionResultType.SUCCESS;
	}
}
