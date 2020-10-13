package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blockentities.InfoPanelBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InfoPanelBlock extends FacingBlock implements BlockEntityProvider {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public InfoPanelBlock() {
		super(FabricBlockSettings.of(Material.METAL).strength(12.0F).lightLevel(state -> state.get(POWERED) ? 10 : 0));
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new InfoPanelBlockEntity();
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
		if (world.isClient)
			return;

		boolean powered = (Boolean) state.get(POWERED);
		boolean power = world.isReceivingRedstonePower(pos);
		if (powered != power)
			world.setBlockState(pos, getDefaultState().with(FACING, state.get(FACING)).with(POWERED, power), 2);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient || hand != Hand.MAIN_HAND)
			return ActionResult.PASS;
		BlockEntity be = world.getBlockEntity(pos);
		if (!(be instanceof InfoPanelBlockEntity))
			return ActionResult.PASS;
		if (EnergyControl.altPressed.get(player.getGameProfile().getId()) && ((InfoPanelBlockEntity) be).getFacing() == hit.getSide())
			if (((InfoPanelBlockEntity) be).runTouchAction(player, pos, hit))
				return ActionResult.SUCCESS;
		NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
		if (factory != null) {
			player.openHandledScreen(factory);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InfoPanelBlockEntity) {
			((InfoPanelBlockEntity) be).setFacing(state.get(FACING));
			((InfoPanelBlockEntity) be).setRotation(Direction.DOWN);
		}
		if (world.isClient)
			return;

		boolean powered = (Boolean) state.get(POWERED);
		boolean power = world.isReceivingRedstonePower(pos);
		if (powered != power)
			world.setBlockState(pos, getDefaultState().with(FACING, state.get(FACING)).with(POWERED, power), 2);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof InfoPanelBlockEntity) {
				ItemScatterer.spawn(world, (BlockPos) pos, (Inventory) ((InfoPanelBlockEntity) be));
				// update comparators
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InfoPanelBlockEntity)
			return (InfoPanelBlockEntity) be;
		return null;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
}
