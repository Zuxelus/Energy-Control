package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.api.IItemKit;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class ItemKitMain extends Item implements IItemKit {

	public ItemKitMain() {
		super(new Item.Settings().group(EnergyControl.ITEM_GROUP).maxCount(16));
	}

	public ActionResult onItemUseFirst(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (!(player instanceof ServerPlayerEntity) || stack.isEmpty())
			return ActionResult.PASS;

		BlockHitResult hitResult = raycast(world, player, RaycastContext.FluidHandling.NONE);
		if (hitResult.getType() != HitResult.Type.BLOCK)
			return ActionResult.PASS;

		ItemStack sensorLocationCard = ((ItemKitMain) stack.getItem()).getSensorCard(stack, player, world, hitResult.getBlockPos(), hitResult.getSide());
		if (sensorLocationCard.isEmpty())
			return ActionResult.PASS;

		stack.decrement(1);
		ItemEntity dropItem = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), sensorLocationCard);
		dropItem.setPickupDelay(0);
		world.spawnEntity(dropItem);
		return ActionResult.SUCCESS;
	}
}
