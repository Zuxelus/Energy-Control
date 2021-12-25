package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IndustrialAlarm extends HowlerAlarm {
	public static final IntProperty LIGHT = IntProperty.of("light", 0, 3);
	private static final int[] lightSteps = { 0, 7, 14, 7, 0};

	public IndustrialAlarm() {
		super(FabricBlockSettings.copyOf(ModItems.settings).luminance(state -> lightSteps[state.get(LIGHT)]));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(LIGHT);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(LIGHT, 0);
	}

	@Override
	protected BlockEntityFacing newBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.industrial_alarm.instantiate(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof TileEntityHowlerAlarm)
				ScreenHandler.openIndustrialAlarmScreen((TileEntityIndustrialAlarm) te);
		}
		return ActionResult.SUCCESS;
	}
}
