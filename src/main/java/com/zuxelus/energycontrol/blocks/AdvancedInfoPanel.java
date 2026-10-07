package com.zuxelus.energycontrol.blocks;

import org.jspecify.annotations.Nullable;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AdvancedInfoPanel extends InfoPanel {

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
	protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
		if (world.isClientSide())
			return;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TileEntityAdvancedInfoPanel)
			((TileEntityAdvancedInfoPanel) be).updatePower();
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) { }

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (!(tile instanceof TileEntityAdvancedInfoPanel))
			return super.getShape(state, world, pos, context);

		int thickness = ((TileEntityAdvancedInfoPanel) tile).thickness;
		if (thickness < 1 || thickness > 16)
			thickness = 16;
		Direction enumfacing = (Direction) state.getValue(FACING);
		if (enumfacing == null)
			return super.getShape(state, world, pos, context);
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
			return super.getShape(state, world, pos, context);
		}
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return InteractionResult.PASS;
		if (!world.isClientSide() && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		if (!world.isClientSide())
			player.openMenu(state.getMenuProvider(world, pos));
		return InteractionResult.SUCCESS;
	}
}
