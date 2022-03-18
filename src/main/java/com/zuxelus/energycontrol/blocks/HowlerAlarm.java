package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.zlib.blocks.FacingBlockSmall;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
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
		super(Block.Properties.of(Material.METAL).strength(1.0F, 3.0F).sound(SoundType.METAL));
	}

	public HowlerAlarm(Properties properties) {
		super(properties);
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.howler_alarm.get().create(pos, state);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide)
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
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof TileEntityHowlerAlarm)
				ScreenHandler.openHowlerAlarmScreen((TileEntityHowlerAlarm) te);
		}
		return InteractionResult.SUCCESS;
	}
}
