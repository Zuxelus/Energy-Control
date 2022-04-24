package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnCtrlTab extends CreativeTabs {
	private static ItemStack itemEnergyKit;

	public EnCtrlTab() {
		super("Energy Control");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		if (itemEnergyKit == null)
			itemEnergyKit = new ItemStack(ModItems.itemKit);
		return itemEnergyKit;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return ModItems.itemKit;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return I18n.format("ec.creativetab");
	}
}
