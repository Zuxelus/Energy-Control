package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitInventory extends ItemKitBase {

	public ItemKitInventory() {
		super(ItemCardType.KIT_INVENTORY, "kit_inventory");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IInventory) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_INVENTORY);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}