package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoloPanelExtender extends HoloPanel {

	@Override
	protected BlockEntityFacing newBlockEntity() {
		return ModTileEntityTypes.holo_panel_extender.instantiate();
	}

	// an extender follows the core panel (TileEntityInfoPanel.updateExtenders), not its own redstone signal
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) { }

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) { }

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient)
			return ActionResult.PASS;
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityHoloPanelExtender))
			return ActionResult.PASS;
		TileEntityInfoPanel panel = ((TileEntityHoloPanelExtender) te).getCore();
		if (panel == null)
			return ActionResult.PASS;
		player.openHandledScreen(panel); // the extender has no GUI of its own, open the core panel's
		return ActionResult.SUCCESS;
	}
}
