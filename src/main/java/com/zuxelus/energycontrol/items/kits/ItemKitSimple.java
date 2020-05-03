package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemKitSimple extends ItemKitBase {

	public ItemKitSimple(int damage, String textureName) {
		super(damage, textureName);
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		BlockPos position = getTargetCoordinates(world, pos, stack);
		if (position == null)
			return ItemStack.EMPTY;	
			
		ItemStack sensorLocationCard = getItemCard();
		if (sensorLocationCard.isEmpty())
			return ItemStack.EMPTY;
		
		ItemStackHelper.setCoordinates(sensorLocationCard, position);
		return sensorLocationCard;
	}

	protected abstract BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack);
	
	protected abstract ItemStack getItemCard();
}
