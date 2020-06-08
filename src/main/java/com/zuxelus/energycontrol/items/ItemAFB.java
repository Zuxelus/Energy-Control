package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import ic2.api.item.IElectricItem;
import ic2.core.item.ItemBattery;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemAFB extends ItemBattery {
	public static final double CAPACITY = 1.0E8D;
	public static final int TIER = 5;
	
	public ItemAFB() {
		super(null, CAPACITY, 32768.0D, TIER);
		setCreativeTab(EnergyControl.creativeTab);
		setRarity(EnumRarity.UNCOMMON);
	}
}