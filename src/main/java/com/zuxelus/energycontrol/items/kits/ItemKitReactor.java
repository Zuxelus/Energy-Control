package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitReactor extends ItemKitBase {
	public ItemKitReactor() {
		super(ItemCardType.KIT_REACTOR, "kit_reactor");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		return CrossModLoader.getCrossMod(ModIDs.IC2).getReactorCard(world, x, y, z);
	}
}
