package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.IItemKit;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public abstract class ItemKitMain extends Item implements IItemKit {

	public ItemKitMain() {
		super(new Item.Properties().stacksTo(16).setNoRepair());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !(player instanceof ServerPlayer))
			return InteractionResult.PASS;

		if (stack.isEmpty())
			return InteractionResult.PASS;
		ItemStack sensorLocationCard = ((ItemKitMain) stack.getItem()).getSensorCard(stack, player, context.getLevel(), context.getClickedPos(), context.getClickedFace());
		if (sensorLocationCard.isEmpty())
			return InteractionResult.PASS;

		stack.shrink(1);
		ItemEntity dropItem = new ItemEntity(context.getLevel(), player.getX(), player.getY(), player.getZ(), sensorLocationCard);
		dropItem.setPickUpDelay(0);
		context.getLevel().addFreshEntity(dropItem);
		return InteractionResult.SUCCESS;
	}
}
