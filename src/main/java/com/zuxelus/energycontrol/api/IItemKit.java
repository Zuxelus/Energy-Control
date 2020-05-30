package com.zuxelus.energycontrol.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public interface IItemKit {

	public int getDamage();
	
	public String getName();
	
	public String getUnlocalizedName();

	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z);

	public Object[] getRecipe();

	public void registerIcon(IIconRegister iconRegister);

	public IIcon getIcon();
}
