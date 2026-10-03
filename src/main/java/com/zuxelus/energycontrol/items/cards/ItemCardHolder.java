package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(new Item.Settings().maxCount(1));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		// main hand only: the container reads the main hand stack and locks the selected hotbar slot
		if (!player.isSneaking() && !world.isClient && hand == Hand.MAIN_HAND && stack.getCount() == 1)
			player.openHandledScreen(new InventoryCardHolder(stack));
		return TypedActionResult.success(stack);
	}
}
