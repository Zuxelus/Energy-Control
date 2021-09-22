package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemKitMekanism extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		CompoundTag tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.MEKANISM_GENERATORS).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.card_mekanism);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
