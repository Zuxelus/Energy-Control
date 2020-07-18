package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitToggle extends ItemKitBase {

	public ItemKitToggle() {
		super(ItemCardType.KIT_TOGGLE, "kit_toggle");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (state != null && (block == Blocks.LEVER || block == Blocks.STONE_BUTTON || block == Blocks.WOODEN_BUTTON)) {
			ItemStack newCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TOGGLE);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return null;
	}
}
