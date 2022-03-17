package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class InfoPanel extends FacingBlockActive {

	public InfoPanel() {
		super(Block.Properties.of(Material.METAL).strength(3.0F).lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0).sound(SoundType.METAL));
	}

	public InfoPanel(Block.Properties builder) {
		super(builder.lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0));
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel.get().create(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return InteractionResult.PASS;
		if (!world.isClientSide && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		if (!world.isClientSide)
				NetworkHooks.openGui((ServerPlayer) player, (TileEntityInfoPanel) te, pos);
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ACTIVE, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isClientSide)
			return;

		boolean flag = state.getValue(ACTIVE);
		if (flag == world.hasNeighborSignal(pos))
			return;

		if (flag)
			world.getBlockTicks().scheduleTick(pos, this, 4);
		else {
			world.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
		if (state.getValue(ACTIVE) && !world.hasNeighborSignal(pos)) {
			world.setBlock(pos, state.cycle(ACTIVE), 2);
			updateExtenders(state, world, pos);
		}
	}

	private void updateExtenders(BlockState state, Level world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TileEntityInfoPanel)
			((TileEntityInfoPanel) be).updateExtenders(world, !state.getValue(ACTIVE));
	}

	/*@Override // TODO
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityFacing)
			return side == ((BlockEntityFacing) te).getFacing();
		return false;
	}*/

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
}