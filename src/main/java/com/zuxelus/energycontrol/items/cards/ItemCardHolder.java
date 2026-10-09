package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(ModItems.itemSettings().stacksTo(1));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && !level.isClientSide() && stack.getCount() == 1)
			player.openMenu(new InventoryCardHolder(stack, hand));
		return InteractionResult.SUCCESS;
	}
}
