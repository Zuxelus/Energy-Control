package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermalMonitor;
import com.zuxelus.energycontrol.tileentities.TileEntityThermalMonitor;
import com.zuxelus.zlib.blocks.FacingHorizontal;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RemoteThermalMonitor extends FacingHorizontal {

	public RemoteThermalMonitor() {
		super(FabricBlockSettings.copyOf(ModItems.settings));
	}

	@Override
	protected BlockEntityFacing newBlockEntity() {
		return ModTileEntityTypes.remote_thermo.instantiate();
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityRemoteThermalMonitor))
			return ActionResult.PASS;
		if (!world.isClient)
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
		return ActionResult.SUCCESS;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView blockAccess, BlockPos pos, Direction side) {
		BlockEntity te = blockAccess.getBlockEntity(pos);
		if (!(te instanceof TileEntityThermalMonitor))
			return 0;
		return ((TileEntityThermalMonitor) te).getPowered() ? 15 : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
}
