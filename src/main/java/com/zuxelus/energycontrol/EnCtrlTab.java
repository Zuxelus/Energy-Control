package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.items.ItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnCtrlTab extends CreativeTabs {
	private static ItemStack itemEnergyKit;

	public EnCtrlTab() {
		super("Energy Control");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		if (itemEnergyKit == null)
			itemEnergyKit = new ItemStack(ItemHelper.itemKit);
		return itemEnergyKit;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ItemHelper.itemKit;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return I18n.format("ec.creativetab");
	}
}