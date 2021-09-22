package com.zuxelus.energycontrol.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public interface IItemKit {
	/**
	 * Called when this item is used on a block, trigerring a {@link PlayerInteractEvent.RightClickBlock}
	 * @return This kit's corresponding card
	 */
	ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side);
}
