package com.zuxelus.energycontrol.items;

import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUpgrade extends Item {
	public static final int DAMAGE_RANGE = 0;
	public static final int DAMAGE_COLOR = 1;

	public ItemUpgrade() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		switch (damage) {
		case DAMAGE_RANGE:
			return "item.ItemRangeUpgrade";
		case DAMAGE_COLOR:
			return "item.ItemColorUpgrade";
		default:
			return "";
		}
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		itemList.add(new ItemStack(item, 1, DAMAGE_RANGE));
		itemList.add(new ItemStack(item, 1, DAMAGE_COLOR));
	}
}
