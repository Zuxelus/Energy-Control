package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.gui.ScreenHandler;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHowlerAlarm;
import com.zuxelus.energycontrol.tileentities.TileEntityIndustrialAlarm;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class IndustrialAlarm extends HowlerAlarm {

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.industrial_alarm.get().create();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityIndustrialAlarm))
			return 0;
		return ((TileEntityIndustrialAlarm)te).lightLevel;
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
