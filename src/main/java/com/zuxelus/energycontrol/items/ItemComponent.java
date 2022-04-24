package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemComponent extends Item {
	public static final int ADVANCED_CIRCUIT = 0;
	public static final int BASIC_CIRCUIT = 1;
	public static final int MACHINE_CASING = 2;
	public static final int RADIO_TRANSMITTER = 3;
	public static final int STRONG_STRING = 4;

	public ItemComponent() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int damage = stack.getItemDamage();
		switch (damage) {
		case ADVANCED_CIRCUIT:
			return "item.advanced_circuit";
		case BASIC_CIRCUIT:
			return "item.basic_circuit";
		case MACHINE_CASING:
			return "item.machine_casing";
		case RADIO_TRANSMITTER:
			return "item.radio_transmitter";
		case STRONG_STRING:
			return "item.strong_string";
		default:
			return "";
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
		items.add(new ItemStack(ModItems.itemComponent, 1, ADVANCED_CIRCUIT));
		items.add(new ItemStack(ModItems.itemComponent, 1, BASIC_CIRCUIT));
		items.add(new ItemStack(ModItems.itemComponent, 1, MACHINE_CASING));
		items.add(new ItemStack(ModItems.itemComponent, 1, RADIO_TRANSMITTER));
		items.add(new ItemStack(ModItems.itemComponent, 1, STRONG_STRING));
	}
}

