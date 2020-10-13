package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class MainKitItem extends Item {

	public MainKitItem() {
		super(new Item.Settings().group(EnergyControl.ITEM_GROUP).maxCount(16));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isEmpty() || stack.getCount() != 1)
			return TypedActionResult.pass(stack);
		
		HitResult hitResult = raycast(world, player, RaycastContext.FluidHandling.NONE);
		if (hitResult.getType() != Type.BLOCK)
			return TypedActionResult.pass(stack);
		
		BlockHitResult blockHitResult = (BlockHitResult)hitResult;
		ItemStack sensorLocationCard = getSensorCard(stack, player, world, blockHitResult.getBlockPos());
		if (sensorLocationCard.isEmpty())
			return TypedActionResult.pass(stack);
		
		//player.equip(player.inventory.selectedSlot, sensorLocationCard);
		return TypedActionResult.success(sensorLocationCard);
	}

	protected ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		return ItemStack.EMPTY;
	}	
}
