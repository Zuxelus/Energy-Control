package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IItemKit {

	ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side);
}
