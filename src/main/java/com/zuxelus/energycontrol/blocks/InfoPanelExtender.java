package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanelExtender;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class InfoPanelExtender extends FacingBlockActive {
	public InfoPanelExtender() {
		this(ModItems.blockSettings());
	}

	public InfoPanelExtender(BlockBehaviour.Properties settings) {
		super(settings.lightLevel(state -> state.getValue(ACTIVE) ? 10 : 0));
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel_extender.create(pos, state);
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide())
			return InteractionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanelExtender))
			return InteractionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
		if (panel == null)
			return InteractionResult.PASS;
		if (EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) panel).getFacing() == hit.getDirection())
			if (((TileEntityInfoPanel) panel).runTouchAction(player.getItemInHand(hand), pos, hit.getLocation()))
				return InteractionResult.SUCCESS;
		player.openMenu(panel); // the extender has no GUI of its own, open the core panel's
		return InteractionResult.SUCCESS;
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(ACTIVE, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos()));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
}
