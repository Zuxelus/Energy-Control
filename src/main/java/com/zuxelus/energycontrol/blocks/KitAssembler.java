package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
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

public class KitAssembler extends FacingHorizontalActive {

	@Override
	public TileEntityFacing createTileEntity() {
		return new TileEntityKitAssembler();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityKitAssembler))
			return ActionResultType.PASS;
		if (!world.isRemote)
			NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityKitAssembler) te, pos);
		return ActionResultType.SUCCESS;
	}
}
