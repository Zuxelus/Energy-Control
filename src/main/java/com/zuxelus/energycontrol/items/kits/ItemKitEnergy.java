package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.EnergyCardHelper;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import ic2.api.item.IC2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitBase {

	public ItemKitEnergy() {
		super(ItemCardType.KIT_ENERGY, "kit_energy");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		EnergyStorageData storage = EnergyCardHelper.getStorageAt(world, pos, EnergyStorageData.TARGET_TYPE_UNKNOWN);
		if (storage == null)
			return ItemStack.EMPTY;

		ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
		ItemStackHelper.setCoordinates(sensorLocationCard, pos, storage.type);
		return sensorLocationCard;
	}
}
