package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.tileentities.TileEntityFacing;
import com.zuxelus.energycontrol.tileentities.TileEntityRangeTrigger;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RangeTrigger extends FacingHorizontal {
	public static final PropertyEnum<EnumState> STATE = PropertyEnum.<EnumState>create("state", EnumState.class);

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
		return new BlockStateContainer(this, new IProperty[] { FACING, STATE });
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return super.getStateForPlacement(world, pos, facing, hitZ, hitZ, hitZ, meta, placer).withProperty(STATE, EnumState.OFF);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(CrossModLoader.ic2.getItemStack("machine"));
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

	public static enum EnumState implements IStringSerializable {
		OFF(0, "off"), ON(1, "on"), ERROR(2, "error");

		private final int id;
		private final String name;

		private EnumState(int id, String name) {
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
