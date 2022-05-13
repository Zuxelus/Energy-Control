package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemKit {
	/**
	 * Called when this item is used on a block, triggering a {@link Item#onItemUseFirst(ItemStack, EntityPlayer, World, int, int, int, int, float, float, float) onItemUseFirst}
	 * @return This kit's corresponding card
	 */
	ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side);
}
