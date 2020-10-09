package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import techreborn.blockentity.storage.fluid.TankUnitBaseBlockEntity;

public class LiquidItemKit extends MainKitItem {

	@Override
	protected ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TankUnitBaseBlockEntity) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.LIQUID_ITEM_CARD);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}
}