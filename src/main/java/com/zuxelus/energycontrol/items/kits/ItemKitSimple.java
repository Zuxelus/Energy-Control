package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemKitSimple extends ItemKitBase {

	public ItemKitSimple(int damage, String textureName) {
		super(damage, textureName);
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		BlockPos position = getTargetCoordinates(world, pos, stack);
		if (position == null)
			return null;

		ItemStack newCard = getItemCard();
		if (newCard == null)
			return null;

		ItemStackHelper.setCoordinates(newCard, position);
		return newCard;
	}

	protected abstract BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack);

	protected abstract ItemStack getItemCard();
}
