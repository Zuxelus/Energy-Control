package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitBase {

	public ItemKitEnergy() {
		super(ItemCardType.KIT_ENERGY, "kit_energy");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		return CrossModLoader.getEnergyCard(world, x, y, z);
	}
}
