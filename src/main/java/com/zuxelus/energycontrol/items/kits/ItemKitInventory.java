package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemKitInventory extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof Inventory) {
			ItemStack newCard = new ItemStack(ModItems.card_inventory);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}