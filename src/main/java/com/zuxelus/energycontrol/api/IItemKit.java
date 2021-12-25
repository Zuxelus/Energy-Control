package com.zuxelus.energycontrol.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IItemKit {
	/**
	 * Called when this item is used on a block, trigerring a {@link PlayerInteractEvent.RightClickBlock}
	 * @return This kit's corresponding card
	 */
	ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side);
}
