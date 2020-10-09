package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import techreborn.blockentity.generator.SolarPanelBlockEntity;

public class GeneratorItemKit extends MainKitItem {

	@Override
	protected ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof SolarPanelBlockEntity) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.GENERATOR_ITEM_CARD);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}
}
