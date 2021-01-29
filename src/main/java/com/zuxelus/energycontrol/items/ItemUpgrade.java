package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemUpgrade extends Item {
	public static final int DAMAGE_RANGE = 0;
	public static final int DAMAGE_COLOR = 1;
	public static final int DAMAGE_TOUCH = 2;

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
			return "item.upgrade_range";
		case DAMAGE_COLOR:
			return "item.upgrade_color";
		case DAMAGE_TOUCH:
			return "item.upgrade_touch";
		default:
			return "";
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab))
			return;
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_RANGE));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_COLOR));
		items.add(new ItemStack(ModItems.itemUpgrade, 1, DAMAGE_TOUCH));
	}
}
