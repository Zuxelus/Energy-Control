package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemKitRedstone extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block != Blocks.AIR) {
			ItemStack newCard = new ItemStack(ModItems.card_redstone);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
