package com.zuxelus.energycontrol.items.kits;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemKitBase {
	private String textureName;
	protected int damage;
	
	public ItemKitBase(int damage, String textureName) {
		this.damage = damage;
		this.textureName = textureName;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public abstract String getUnlocalizedName();

	protected abstract ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos);
}
