package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
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

public class ThermalMonitor extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.makeCuboidShape(1.0F, 9.0F, 1.0F, 15.0F, 1.0F, 15.0F);
	protected static final VoxelShape AABB_UP = Block.makeCuboidShape(1.0F, 0.0F, 1.0F, 15.0F, 7.0F, 15.0F);
	protected static final VoxelShape AABB_NORTH = Block.makeCuboidShape(1.0F, 1.0F, 9.0F, 15.0F, 15.0F, 1.0F);
	protected static final VoxelShape AABB_SOUTH = Block.makeCuboidShape(1.0F, 1.0F, 0.0F, 15.0F, 15.0F, 7.0F);
	protected static final VoxelShape AABB_WEST = Block.makeCuboidShape(9.0F, 1.0F, 1.0F, 1.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_EAST = Block.makeCuboidShape(0.0F, 1.0F, 1.0F, 7.0F, 15.0F, 15.0F);

	public ThermalMonitor() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F));
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return new TileEntityThermalMonitor();
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityThermalMonitor))
			return 0;
		return ((TileEntityThermalMonitor) te).getPowered() ? side != state.get(FACING) ? 15 : 0 : 0;
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
			if (te instanceof TileEntityThermalMonitor)
				ScreenHandler.openThermalMonitorScreen((TileEntityThermalMonitor) te);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}
