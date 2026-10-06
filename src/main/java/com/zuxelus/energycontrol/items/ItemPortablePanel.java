package com.zuxelus.energycontrol.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemPortablePanel extends Item {

	public ItemPortablePanel(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && !level.isClientSide() && stack.getCount() == 1)
			player.openMenu(new InventoryPortablePanel(stack, hand), buf -> buf.writeBoolean(hand == InteractionHand.OFF_HAND));
		return InteractionResult.SUCCESS;
	}
}
