package com.zuxelus.zlib.blocks;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FacingBlockActive extends FacingBlock {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(ACTIVE, meta > 5);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE) ? super.getMetaFromState(state) + 6 : super.getMetaFromState(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, ACTIVE);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return super.getStateForPlacement(world, pos, facing, hitZ, hitZ, hitZ, meta, placer, stack).withProperty(ACTIVE, false);
	}
}
