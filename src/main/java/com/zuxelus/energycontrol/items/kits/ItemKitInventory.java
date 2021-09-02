package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitInventory extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof IInventory) {
			ItemStack newCard = new ItemStack(ModItems.card_inventory.get());
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}