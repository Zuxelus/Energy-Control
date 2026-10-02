package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.blocks.FacingHorizontalActive;
import com.zuxelus.zlib.blocks.FacingBlock;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class KitAssembler extends FacingHorizontalActive {
    @Override protected com.mojang.serialization.MapCodec<? extends KitAssembler> codec() { return simpleCodec(KitAssembler::new); }


	public KitAssembler(Block.Properties properties) {
		super(FacingBlock.metal(properties));
	}

	@Override
	protected BlockEntityFacing createBlockEntity(BlockPos pos, BlockState state) {
		return ModTileEntityTypes.kit_assembler.get().create(pos, state);
	}

	@Override
	protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof TileEntityKitAssembler))
			return InteractionResult.PASS;
		if (!world.isClientSide)
			player.openMenu((TileEntityKitAssembler) te, buf -> buf.writeBlockPos(pos));
		return InteractionResult.SUCCESS;
	}
}
