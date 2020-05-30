package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitAppEng extends ItemKitBase {
	public ItemKitAppEng() {
		super(ItemCardType.KIT_APPENG, "kit_app_eng");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		return null;
		//return CrossModLoader.crossAppEng.getSensorCard(stack, world, pos);
	}
}
