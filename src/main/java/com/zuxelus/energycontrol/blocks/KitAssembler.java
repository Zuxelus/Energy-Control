package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class KitAssembler extends FacingHorizontalActiveEC {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntityKitAssembler();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("machine"));
		return drops;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.GUI_KIT_ASSEMBER;
	}
}
