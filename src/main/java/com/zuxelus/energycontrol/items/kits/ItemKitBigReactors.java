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

public class ItemKitBigReactors extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		CompoundTag tag = CrossModLoader.getCrossMod(ModIDs.BIG_REACTORS).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.BIGGER_REACTORS).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.card_big_reactors);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
