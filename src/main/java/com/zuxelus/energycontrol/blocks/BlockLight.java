package com.zuxelus.energycontrol.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;

import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLight extends Block implements IWrenchable {
	public static final int DAMAGE_WHITE_OFF = 0;
	public static final int DAMAGE_WHITE_ON = 1;
	public static final int DAMAGE_ORANGE_OFF = 2;
	public static final int DAMAGE_ORANGE_ON = 3;
	public static final int DAMAGE_MAX = 3;

	public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, DAMAGE_MAX);

	public static Map<Integer, Boolean> blocks;

	public BlockLight() {
		super(Material.REDSTONE_LIGHT);
		blocks = new HashMap<Integer, Boolean>();
		this.setHardness(0.3F);
		this.setCreativeTab(EnergyControl.creativeTab);
		setSoundType(SoundType.GLASS);
		register(DAMAGE_WHITE_OFF, false);
		register(DAMAGE_WHITE_ON, true);
		register(DAMAGE_ORANGE_OFF, false);
		register(DAMAGE_ORANGE_ON, true);
	}

	public void register(int damage, boolean isOn) {
		blocks.put(damage, isOn);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { TYPE });
	}

	@Override
	public final IBlockState getStateFromMeta(final int meta) {
		return this.getDefaultState().withProperty(TYPE, meta);
	}

	@Override
	public final int getMetaFromState(final IBlockState state) {
		return state.getValue(TYPE);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		int meta = getMetaFromState(state);
		if (meta == 1 || meta % 2 == 1)
			return 15;
		return 0;
	}

	@Override
	public int damageDropped(IBlockState state) {
		int i = getMetaFromState(state);
		if (i % 2 == 0)
			return i;
		return i - 1;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		updateBlockState(world, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		updateBlockState(world, pos, state);
	}

	private void updateBlockState(World world, BlockPos pos, IBlockState state) {
		if (world.isRemote)
			return;
		int meta = getMetaFromState(state);
		if (meta % 2 == 1) {
			if (world.isBlockIndirectlyGettingPowered(pos) == 0)
				world.setBlockState(pos, getStateFromMeta(meta - 1));
		} else if (world.isBlockIndirectlyGettingPowered(pos) > 0)
			world.setBlockState(pos, getStateFromMeta(meta + 1));
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int i = 0; i <= DAMAGE_MAX; i++)
			if (i % 2 == 0)
				items.add(new ItemStack(this, 1, i));
	}

	// IWrenchable
	@Override
	public EnumFacing getFacing(World world, BlockPos pos) {
		return EnumFacing.UP;
	}

	@Override
	public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
		return getDrops(world, pos, state, 1);
	}
}
