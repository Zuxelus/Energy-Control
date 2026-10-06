package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.api.IItemKit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class ItemKitMain extends Item implements IItemKit {

	public ItemKitMain() {
		super(ModItems.itemSettings().stacksTo(16));
	}

	public InteractionResult onItemUseFirst(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!(player instanceof ServerPlayer) || stack.isEmpty())
			return InteractionResult.PASS;

		BlockHitResult hitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
		if (hitResult.getType() != HitResult.Type.BLOCK)
			return InteractionResult.PASS;

		ItemStack sensorLocationCard = ((ItemKitMain) stack.getItem()).getSensorCard(stack, player, world, hitResult.getBlockPos(), hitResult.getDirection());
		if (sensorLocationCard.isEmpty())
			return InteractionResult.PASS;

		stack.shrink(1);
		ItemEntity dropItem = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), sensorLocationCard);
		dropItem.setPickUpDelay(0);
		world.addFreshEntity(dropItem);
		return InteractionResult.SUCCESS;
	}
}
