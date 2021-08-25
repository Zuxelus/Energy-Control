package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemKit {

	int getDamage();
	
	String getName();
	
	String getUnlocalizedName();

	ItemStack getSensorCard(ItemStack stack, Item card, PlayerEntity player, World world, BlockPos pos);

	Object[] getRecipe();
}
