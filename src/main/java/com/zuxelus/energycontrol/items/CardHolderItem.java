package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CardHolderItem extends Item {

	public CardHolderItem() {
		super(new Item.Settings().maxCount(1).group(EnergyControl.ITEM_GROUP));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (!player.isSneaking() && !world.isClient && stack.getCount() == 1) {
			player.openHandledScreen(new CardHolderInventory(stack));
			return TypedActionResult.consume(stack);
		}
		return TypedActionResult.pass(stack);
	}
}
