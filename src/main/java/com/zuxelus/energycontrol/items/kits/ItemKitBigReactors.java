package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitBigReactors extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		TileEntity te = world.getBlockEntity(pos);
		CompoundNBT tag = CrossModLoader.getCrossMod(ModIDs.BIG_REACTORS).getCardData(te);
		if (tag == null)
			tag = CrossModLoader.getCrossMod(ModIDs.BIGGER_REACTORS).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.card_big_reactors.get());
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
