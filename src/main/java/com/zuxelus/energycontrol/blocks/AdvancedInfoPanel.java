package com.zuxelus.energycontrol.blocks;

import com.mojang.serialization.MapCodec;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

public class AdvancedInfoPanel extends InfoPanel {
	public static final MapCodec<AdvancedInfoPanel> CODEC = simpleCodec(settings -> new AdvancedInfoPanel());

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public AdvancedInfoPanel() {
		// the shape follows the panel thickness, so it must not be cached per block state
		super(ModItems.blockSettings().dynamicShape().noOcclusion());
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_advanced.create(pos, state);
	}

	// on/off depends on the power mode, so the block entity sets ACTIVE (light) instead of the redstone signal
	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.isClientSide)
			return;
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TileEntityAdvancedInfoPanel)
			((TileEntityAdvancedInfoPanel) be).updatePower();
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) { }

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return super.getShape(state, level, pos, context);

		int thickness = ((TileEntityAdvancedInfoPanel) tile).thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		Direction enumfacing = state.getValue(FACING);
		if (enumfacing == null)
			return super.getShape(state, level, pos, context);
		switch (enumfacing) {
		case EAST:
			return Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D);
		case WEST:
			return Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		case SOUTH:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
		case NORTH:
			return Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
		case UP:
			return Block.box(0.0D, 0.0D, 0.0D, 16.0D, thickness, 16.0D);
		case DOWN:
			return Block.box(0.0D, 16.0D - thickness, 0.0D, 16.0D, 16.0D, 16.0D);
		default:
			return super.getShape(state, level, pos, context);
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return InteractionResult.PASS;
		if (!level.isClientSide && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getMainHandItem(), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		if (!level.isClientSide)
			player.openMenu(state.getMenuProvider(level, pos));
		return InteractionResult.SUCCESS;
	}
}
