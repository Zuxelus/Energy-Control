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
	public InteractionResult use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		// main hand only: the container reads the main hand stack and locks the selected hotbar slot
		if (!player.isShiftKeyDown() && !world.isClientSide() && hand == InteractionHand.MAIN_HAND && stack.getCount() == 1)
			player.openMenu(new InventoryCardHolder(stack));
		return InteractionResult.SUCCESS;
	}
}
