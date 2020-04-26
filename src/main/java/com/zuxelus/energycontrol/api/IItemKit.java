package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemKit {

	public int getDamage();
	
	public String getName();
	
	public String getUnlocalizedName();

	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos);

	public Object[] getRecipe();
}
