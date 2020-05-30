package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitBase {

	public ItemKitEnergy() {
		super(ItemCardType.KIT_ENERGY, "kit_energy");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack result = CrossModLoader.ic2.getEnergyCard(world, x, y, z);
		if (result != null)
			return result;
		return null;
	}
}
