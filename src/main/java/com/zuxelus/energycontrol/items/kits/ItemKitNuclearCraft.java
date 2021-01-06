package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import nc.tile.generator.TileSolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitNuclearCraft extends ItemKitBase {

	public ItemKitNuclearCraft() {
		super(ItemCardType.KIT_NUCLEARCRAFT, "kit_nuclearcraft");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (/*te instanceof TileDecayGenerator ||*/ te instanceof TileSolarPanel /*|| te instanceof TileItemProcessor || te instanceof TileItemFluidProcessor || te instanceof TileBattery || te instanceof TileFluidProcessor || te instanceof TileFissionController*/) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_NUCLEARCRAFT);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}