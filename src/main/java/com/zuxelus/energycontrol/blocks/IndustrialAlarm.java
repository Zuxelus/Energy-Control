package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class IndustrialAlarm extends HowlerAlarm {
	public static final IntegerProperty LIGHT = IntegerProperty.create("light", 0, 3);
	private static final int[] lightSteps = { 0, 7, 14, 7, 0};

	public IndustrialAlarm() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(3.0F).sound(SoundType.METAL));
	}

	@Override
	public int getLightValue(BlockState state) { // in 1.16 changed to ToIntFunction<>
		return lightSteps[state.get(LIGHT)];
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LIGHT);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(LIGHT, 0);
	}

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.industrial_alarm.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileEntityHowlerAlarm)
				ScreenHandler.openIndustrialAlarmScreen((TileEntityIndustrialAlarm) te);
		}
		return ActionResultType.SUCCESS;
	}
}
