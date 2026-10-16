package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class KitAssembler extends FacingHorizontalActive {
	public KitAssembler() {
		super(ModItems.blockSettings());
	}

	@Override
	protected BlockEntityFacing createFacingBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.kit_assembler.create(pos, state);
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = level.getBlockEntity(pos);
		if (!(te instanceof TileEntityKitAssembler))
			return InteractionResult.PASS;
		if (!level.isClientSide())
			player.openMenu(state.getMenuProvider(level, pos));
		return InteractionResult.SUCCESS;
	}
}
