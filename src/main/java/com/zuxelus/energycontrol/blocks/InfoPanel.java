package com.zuxelus.energycontrol.blocks;

import java.util.Random;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.blocks.FacingBlockActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InfoPanel extends FacingBlockActive {

	public InfoPanel() {
		super(FabricBlockSettings.copyOf(ModItems.settings).luminance(state -> state.get(ACTIVE) ? 10 : 0));
	}

	public InfoPanel(AbstractBlock.Settings settings) {
		super(settings.luminance(state -> state.get(ACTIVE) ? 10 : 0));
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.info_panel.instantiate(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityInfoPanel))
			return ActionResult.PASS;
		if (!world.isClient && EnergyControl.altPressed.get(player) && ((TileEntityInfoPanel) te).getFacing() == hit.getSide())
			if (((TileEntityInfoPanel) te).runTouchAction(player.getStackInHand(hand), pos, hit.getPos()))
				return ActionResult.SUCCESS;
		if (!world.isClient)
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(ACTIVE, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (world.isClient)
			return;

		boolean flag = state.get(ACTIVE);
		if (flag == world.isReceivingRedstonePower(pos))
			return;

		if (flag)
			world.createAndScheduleBlockTick(pos, this, 4);
		else
			world.setBlockState(pos, state.cycle(ACTIVE), 2);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(ACTIVE).booleanValue() && !world.isReceivingRedstonePower(pos))
			world.setBlockState(pos, state.cycle(ACTIVE), 2);
	}

	/*@Override // TODO
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityFacing)
			return side == ((BlockEntityFacing) te).getFacing();
		return false;
	}*/

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}