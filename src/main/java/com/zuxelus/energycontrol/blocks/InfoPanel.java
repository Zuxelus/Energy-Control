package com.zuxelus.energycontrol.blocks;

import org.jspecify.annotations.Nullable;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class InfoPanel extends FacingBlockActive {
	public InfoPanel() {
		super(ModItems.blockSettings().lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0));
	}

	public InfoPanel(Properties properties) {
		super(properties.lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0));
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel.create(pos, state);
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return InteractionResult.PASS;
		if (!level.isClientSide() && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		if (!level.isClientSide())
			player.openMenu(state.getMenuProvider(level, pos));
		return InteractionResult.SUCCESS;
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
		if (level.isClientSide())
			return;

		boolean flag = state.getValue(ACTIVE);
		if (flag == level.hasNeighborSignal(pos))
			return;

		if (flag)
			level.scheduleTick(pos, this, 4);
		else {
			level.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, level, pos);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(ACTIVE).booleanValue() && !level.hasNeighborSignal(pos)) {
			level.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, level, pos);
		}
	}

	private void updateExtenders(BlockState state, Level level, BlockPos pos) { // server
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(level, !state.getValue(ACTIVE));
	}

	/*@Override // TODO
	public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof BlockEntityFacing)
			return side == ((BlockEntityFacing) te).getFacing();
		return false;
	}*/

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
