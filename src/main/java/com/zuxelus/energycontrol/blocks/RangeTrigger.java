package com.zuxelus.energycontrol.blocks;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RangeTrigger extends FacingHorizontalEC {
	public static final PropertyEnum<EnumState> STATE = PropertyEnum.create("state", EnumState.class);

	@Override
	public TileEntityFacing createTileEntity(int meta) {
		return new TileEntityRangeTrigger();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(STATE, EnumState.getState(meta / 4));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) + 4 * state.getValue(STATE).getId();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, STATE);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return super.getStateForPlacement(world, pos, facing, hitZ, hitZ, hitZ, meta, placer, stack).withProperty(STATE, EnumState.OFF);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("machine"));
		return drops;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		TileEntity te = blockAccess.getTileEntity(pos);
		if (!(te instanceof TileEntityRangeTrigger))
			return 0;
		return ((TileEntityRangeTrigger) te).getPowered() ? 15 : 0;
	}

	@Override
	protected int getBlockGuiId() {
		return BlockDamages.DAMAGE_RANGE_TRIGGER;
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	public enum EnumState implements IStringSerializable {
		OFF(0, "off"), ON(1, "on"), ERROR(2, "error");

		private final int id;
		private final String name;

		EnumState(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		@Override
		public String getName() {
			return name;
		}

		public static EnumState getState(int id) {
			switch (id) {
			default:
				return OFF;
			case 1:
				return ON;
			case 2:
				return ERROR;
			}
		}
	}
}
