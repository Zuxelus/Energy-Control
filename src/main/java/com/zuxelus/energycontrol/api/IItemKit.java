package com.zuxelus.energycontrol.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IItemKit {

	ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side);
}
