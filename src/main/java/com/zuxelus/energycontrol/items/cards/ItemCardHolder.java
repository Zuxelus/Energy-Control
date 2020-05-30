package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
		setTextureName(EnergyControl.MODID + ":" + "card_holder");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!player.isSneaking() && !world.isRemote && stack.stackSize == 1)
			player.openGui(EnergyControl.instance, BlockDamages.GUI_CARD_HOLDER, world, 0, 0, 0);
		return stack;
	}
}
