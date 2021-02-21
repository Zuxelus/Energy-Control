package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemKitMain extends Item {

	public ItemKitMain() {
		super(new Item.Properties().group(EnergyControl.ITEM_GROUP).maxStackSize(16).setNoRepair());
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		// TODO Auto-generated method stub
		return super.onItemUse(context);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null || !(player instanceof ServerPlayerEntity))
			return ActionResultType.PASS;

		if (stack.isEmpty())
			return ActionResultType.PASS;
		ItemStack sensorLocationCard = ((ItemKitMain) stack.getItem()).getSensorCard(stack, player, context.getWorld(), context.getPos(), context.getFace());
		if (sensorLocationCard.isEmpty())
			return ActionResultType.PASS;
		ItemEntity dropItem = new ItemEntity(context.getWorld(), player.getPosX(), player.getPosY(), player.getPosZ(), sensorLocationCard);
		dropItem.setPickupDelay(0);
		context.getWorld().addEntity(dropItem);
		return ActionResultType.SUCCESS;
	}

	public abstract ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side);
}
