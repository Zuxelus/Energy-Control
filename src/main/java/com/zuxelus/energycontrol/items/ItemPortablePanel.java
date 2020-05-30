package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemPortablePanel extends Item {

	public ItemPortablePanel() {
		super();
		setCreativeTab(EnergyControl.creativeTab);
		setTextureName(EnergyControl.MODID + ":" + "portable_panel");
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.isSneaking() && !world.isRemote && stack.stackSize == 1)
			player.openGui(EnergyControl.instance, BlockDamages.GUI_PORTABLE_PANEL, world, 0, 0, 0);
		return stack;
	}
}
