package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityHoloPanelExtender;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class HoloPanelExtender extends HoloPanel {

	@Override
	protected TileEntityFacing createTileEntity() {
		return ModTileEntityTypes.holo_panel_extender.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote)
			return ActionResultType.PASS;
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityHoloPanelExtender))
			return ActionResultType.PASS;
		TileEntityInfoPanel panel = ((TileEntityHoloPanelExtender) te).getCore();
		if (panel == null)
			return ActionResultType.PASS;
		NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityInfoPanel) panel, pos);
		return ActionResultType.SUCCESS;
	}
}
