package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		return CrossModLoader.getEnergyCard(world, pos);
	}
}
