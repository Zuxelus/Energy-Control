package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitReactor extends ItemKitBase {
	public ItemKitReactor() {
		super(ItemCardType.KIT_REACTOR, "kit_reactor");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		return CrossModLoader.ic2.getReactorCard(world, pos);
	}
}
