package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import ic2.api.item.IElectricItem;
import ic2.core.item.ItemBattery;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAFB extends Item implements IElectricItem {
	public static final double CAPACITY = 1.0E8D;
	public static final double TRANSFER = 32768.0D;
	public static final int TIER = 5;
	
	public ItemAFB() {
		super();
		setCreativeTab(EnergyControl.creativeTab);
		setMaxStackSize(1);
		setTextureName(EnergyControl.MODID + ":" + "afb");
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.uncommon;
	}
	
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
}