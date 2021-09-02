package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public abstract class ItemKitMain extends Item implements IItemKit {

	public ItemKitMain() {
		super(new Item.Properties().tab(EnergyControl.ITEM_GROUP).stacksTo(16).setNoRepair());
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		// TODO Auto-generated method stub
		return super.useOn(context);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null || !(player instanceof ServerPlayerEntity))
			return ActionResultType.PASS;

		if (stack.isEmpty())
			return ActionResultType.PASS;
		ItemStack sensorLocationCard = ((ItemKitMain) stack.getItem()).getSensorCard(stack, player, context.getLevel(), context.getClickedPos(), context.getClickedFace());
		if (sensorLocationCard.isEmpty())
			return ActionResultType.PASS;

		stack.shrink(1);
		ItemEntity dropItem = new ItemEntity(context.getLevel(), player.getX(), player.getY(), player.getZ(), sensorLocationCard);
		dropItem.setPickUpDelay(0);
		context.getLevel().addFreshEntity(dropItem);
		return ActionResultType.SUCCESS;
	}
}
