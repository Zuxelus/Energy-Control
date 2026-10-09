package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemKitEnergy extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
		return CrossModLoader.getEnergyCard(world, pos);
	}
}
