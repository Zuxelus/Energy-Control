package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitRedstone extends ItemKitBase {

	public ItemKitRedstone() {
		super(ItemCardType.KIT_REDSTONE, "kit_redstone");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if (block != Blocks.air) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_REDSTONE);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}
