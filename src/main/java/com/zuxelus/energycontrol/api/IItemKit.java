package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemKit {

	int getDamage();
	
	String getName();
	
	String getUnlocalizedName();

	ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos, EnumFacing side);
}
