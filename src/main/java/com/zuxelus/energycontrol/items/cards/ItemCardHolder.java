package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(new Item.Settings().group(EnergyControl.ITEM_GROUP).maxCount(1));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (!player.isSneaking() && !world.isClient && stack.getCount() == 1)
			player.openHandledScreen(new InventoryCardHolder(stack));
		return TypedActionResult.success(stack);
	}
}
