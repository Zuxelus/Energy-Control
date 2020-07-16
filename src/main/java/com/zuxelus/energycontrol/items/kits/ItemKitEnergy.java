package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitBase {

	public ItemKitEnergy() {
		super(ItemCardType.KIT_ENERGY, "kit_energy");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		ItemStack result = CrossModLoader.ic2.getEnergyCard(world, pos);
		if (!result.isEmpty())
			return result;
		result = CrossModLoader.techReborn.getEnergyCard(world, pos);
		if (!result.isEmpty())
			return result;
		result = CrossModLoader.appEng.getEnergyCard(world, pos);
		if (!result.isEmpty())
			return result;
		result = CrossModLoader.galacticraft.getEnergyCard(world, pos);
		return result;
	}
}
