package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThermalMonitor extends FacingBlockSmall {
	protected static final VoxelShape AABB_DOWN = Block.box(1.0F, 9.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_UP = Block.box(1.0F, 0.0F, 1.0F, 15.0F, 7.0F, 15.0F);
	protected static final VoxelShape AABB_NORTH = Block.box(1.0F, 1.0F, 9.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_SOUTH = Block.box(1.0F, 1.0F, 0.0F, 15.0F, 15.0F, 7.0F);
	protected static final VoxelShape AABB_WEST = Block.box(9.0F, 1.0F, 1.0F, 15.0F, 15.0F, 15.0F);
	protected static final VoxelShape AABB_EAST = Block.box(0.0F, 1.0F, 1.0F, 7.0F, 15.0F, 15.0F);

	public ThermalMonitor() {
		super(Block.Properties.of(Material.METAL).strength(1.0F, 3.0F).sound(SoundType.METAL));
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.thermal_monitor.get().create(pos, state);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityThermalMonitor))
			return 0;
		return ((TileEntityThermalMonitor) te).getPowered() ? side != state.getValue(FACING) ? 15 : 0 : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof TileEntityThermalMonitor)
				ScreenHandler.openThermalMonitorScreen((TileEntityThermalMonitor) te);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
}
