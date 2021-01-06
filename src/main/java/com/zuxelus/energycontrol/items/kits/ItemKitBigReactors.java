package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitBigReactors extends ItemKitBase {

	public ItemKitBigReactors() {
		super(ItemCardType.KIT_BIG_REACTORS, "kit_big_reactors");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityReactorPartBase || te instanceof TileEntityTurbinePartBase) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_BIG_REACTORS);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}
