package com.zuxelus.energycontrol.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLight extends Block {
	public static final int DAMAGE_WHITE_OFF = 0;
	public static final int DAMAGE_WHITE_ON = 1;
	public static final int DAMAGE_ORANGE_OFF = 2;
	public static final int DAMAGE_ORANGE_ON = 3;
	public static final int DAMAGE_MAX = 3;

	public static Map<Integer, Boolean> blocks;
	private IIcon[] icon;

	public BlockLight() {
		super(Material.redstoneLight);
		blocks = new HashMap<Integer, Boolean>();
		this.setHardness(0.3F);
		this.setCreativeTab(EnergyControl.creativeTab);
		setStepSound(soundTypeGlass);
		register(DAMAGE_WHITE_OFF, false);
		register(DAMAGE_WHITE_ON, true);
		register(DAMAGE_ORANGE_OFF, false);
		register(DAMAGE_ORANGE_ON, true);
		icon = new IIcon[blocks.size() + 1];
	}

	public void register(int damage, boolean isOn) {
		blocks.put(damage, isOn);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 1 || meta % 2 == 1)
			return 15;
		return 0;
	}

	@Override
	public int damageDropped(int i) {
		if (i % 2 == 0)
			return i;
		return i - 1;
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return icon[metadata];
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (int i = 0; i < blocks.size(); i++)
			icon[i] = register.registerIcon(EnergyControl.MODID + ":light/lamp" + i);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		updateBlockState(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		updateBlockState(world, x, y, z);
	}

	private void updateBlockState(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		int meta = world.getBlockMetadata(x, y, z);
		if (meta % 2 == 1) {
			if (!world.isBlockIndirectlyGettingPowered(x, y, z))
				world.setBlock(x, y, z, this, meta - 1, 2);
		} else if (world.isBlockIndirectlyGettingPowered(x, y, z))
			world.setBlock(x, y, z, this, meta + 1, 2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item id, CreativeTabs tab, List items) {
		for (int i = 0; i <= DAMAGE_MAX; i++)
			if (i % 2 == 0)
				items.add(new ItemStack(this, 1, i));
	}
}
