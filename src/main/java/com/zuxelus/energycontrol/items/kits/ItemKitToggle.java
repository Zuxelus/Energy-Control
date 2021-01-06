package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKitToggle extends ItemKitBase {

	public ItemKitToggle() {
		super(ItemCardType.KIT_TOGGLE, "kit_toggle");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block == Blocks.lever || block == Blocks.stone_button || block == Blocks.wooden_button) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_TOGGLE);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}
