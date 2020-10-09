package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToggleItemKit extends MainKitItem {

	@Override
	protected ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (state != null && (block == Blocks.LEVER || block instanceof AbstractButtonBlock)) {
			ItemStack newCard = new ItemStack(ModItems.TOGGLE_ITEM_CARD);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}