package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class RangeTrigger extends FacingHorizontal {
	public static final EnumProperty<EnumState> STATE = EnumProperty.create("state", EnumState.class);

	@Override
	public TileEntityFacing createTileEntity() {
		return new TileEntityRangeTrigger();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STATE);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(STATE, EnumState.OFF);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityRangeTrigger))
			return ActionResultType.PASS;
		if (!world.isClientSide)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityRangeTrigger) te, pos);
		return ActionResultType.SUCCESS;
	}

	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		TileEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityRangeTrigger))
			return 0;
		return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	public enum EnumState implements IStringSerializable {
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
		public String getSerializedName() {
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
