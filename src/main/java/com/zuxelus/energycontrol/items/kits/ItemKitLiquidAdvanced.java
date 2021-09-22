package com.zuxelus.energycontrol.items.kits;

import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemKitLiquidAdvanced extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
		List<FluidInfo> list = CrossModLoader.getAllTanks(world, pos);
		if (list != null && list.size() >= 1) {
			ItemStack card = new ItemStack(ModItems.card_liquid_advanced.get());
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}
}
