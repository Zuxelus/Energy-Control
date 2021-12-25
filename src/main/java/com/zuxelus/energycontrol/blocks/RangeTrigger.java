package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RangeTrigger extends FacingHorizontal {
	public static final EnumProperty<EnumState> STATE = EnumProperty.of("state", EnumState.class);

	public RangeTrigger() {
		super(FabricBlockSettings.copyOf(ModItems.settings));
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.range_trigger.instantiate(pos, state);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(STATE);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(STATE, EnumState.OFF);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityRangeTrigger))
			return ActionResult.PASS;
		if (!world.isClient)
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
		BlockEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityRangeTrigger))
			return 0;
		return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	public enum EnumState implements StringIdentifiable {
		OFF(0, "off"), ON(1, "on"), ERROR(2, "error");

		private final int id;
		private final String name;

		EnumState(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		@Override
		public String asString() {
			return name;
		}

		public static EnumState getState(int id) {
			switch (id) {
			default:
				return OFF;
			case 1:
				return ON;
			case 2:
				return ERROR;
			}
		}
	}
}
