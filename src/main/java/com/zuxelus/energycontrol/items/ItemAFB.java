package com.zuxelus.energycontrol.items;

import java.util.LinkedList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAFB extends Item implements IElectricItem, IItemHudInfo {
	public static final double CAPACITY = 1.0E8D;
	public static final double TRANSFER = 32768.0D;
	public static final int TIER = 5;

	public ItemAFB() {
		super();
		setCreativeTab(EnergyControl.creativeTab);
		setMaxDamage(27);
		setMaxStackSize(1);
		setNoRepair();
		setTextureName(EnergyControl.MODID + ":" + "afb");
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.uncommon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		ItemStack charged = new ItemStack(this, 1);
		ElectricItem.manager.charge(charged, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, false);
		list.add(charged);
		ItemStack empty = new ItemStack(this, 1);
		ElectricItem.manager.charge(empty, 0.0D, Integer.MAX_VALUE, true, false);
		list.add(empty);
	}

	//IElectricItem
	@Override
	public boolean canProvideEnergy(ItemStack stack){
		return true;
	}

	@Override
	public double getMaxCharge(ItemStack stack){
		return CAPACITY;
	}

	@Override
	public int getTier(ItemStack stack){
		return TIER;
	}

	@Override
	public double getTransferLimit(ItemStack stack){
		return TRANSFER;
	}

	@Override
	public Item getChargedItem(ItemStack stack) {
		return this;
	}

	@Override
	public Item getEmptyItem(ItemStack stack) {
		return this;
	}

	//IItemHudInfo
	@Override
	public List<String> getHudInfo(ItemStack stack) {
		List<String> info = new LinkedList<String>();
		info.add(ElectricItem.manager.getToolTip(stack));
		return info;
	}
}