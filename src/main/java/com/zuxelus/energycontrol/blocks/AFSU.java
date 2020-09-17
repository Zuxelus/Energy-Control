package com.zuxelus.energycontrol.blocks;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

public class AFSU extends BlockBase {
	private IIcon[] icons = new IIcon[3];

	public AFSU() {
		super(BlockDamages.DAMAGE_AFSU, "afsu");
	}

	@Override
	public TileEntity createNewTileEntity() {
		TileEntityAFSU te = new TileEntityAFSU();
		te.setFacing(0);
		return te;
	}

	@Override
	public IIcon getIconFromSide(int side) {
		switch (side) {
		case 1:
		case 2:
			return icons[side];
		default:
			return icons[0];
		}
	}

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		icons[0] = registerIcon(iconRegister,"afsu/side");
		icons[1] = registerIcon(iconRegister,"afsu/face");
		icons[2] = registerIcon(iconRegister,"afsu/top");
	}

	public static ItemStack getStackwithEnergy(double energy) {
		ItemStack stack = new ItemStack(ItemHelper.blockMain, 1, BlockDamages.DAMAGE_AFSU);
		NBTTagCompound tag = new NBTTagCompound();
		stack.setTagCompound(tag);
		tag.setDouble("energy", energy);
		return stack;
	}
}
