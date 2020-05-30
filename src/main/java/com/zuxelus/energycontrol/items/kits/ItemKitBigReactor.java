package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitBigReactor extends ItemKitBase {

	public ItemKitBigReactor() {
		super(ItemCardType.KIT_BIGREACTOR, "kit_big_reactor");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		return null;
		//return CrossModLoader.crossBigReactors.getSensorCard(stack, world, pos);
	}
}
