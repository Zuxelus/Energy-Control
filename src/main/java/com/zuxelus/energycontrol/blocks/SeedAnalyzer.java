package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedAnalyzer;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeedAnalyzer extends FacingHorizontalActive {

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntitySeedAnalyzer();
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_SEED_ANALYZER;
	}
}
