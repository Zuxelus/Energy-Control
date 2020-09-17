package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitGenerator extends ItemKitBase {

	public ItemKitGenerator() {
		super(ItemCardType.KIT_GENERATOR, "kit_generator");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack result = CrossModLoader.ic2.getGeneratorCard(world, x, y, z);
		if (result != null)
			return result;
		return CrossModLoader.techReborn.getGeneratorCard(world, x, y, z);
	}
}
