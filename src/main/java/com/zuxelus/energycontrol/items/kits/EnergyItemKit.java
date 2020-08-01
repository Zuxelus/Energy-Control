package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.reborn.energy.EnergyStorage;

public class EnergyItemKit extends MainKitItem {

	@Override
	protected ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof EnergyStorage) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.ENERGY_ITEM_CARD);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}
}
