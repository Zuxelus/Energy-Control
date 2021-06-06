package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.tileentities.TileEntityFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class KitAssembler extends FacingHorizontalActive {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntityKitAssembler();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("machine"));
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.GUI_KIT_ASSEMBER;
	}
}
