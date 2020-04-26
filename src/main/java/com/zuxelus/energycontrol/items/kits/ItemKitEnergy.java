package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.EnergyCardHelper;
import com.zuxelus.energycontrol.crossmod.EnergyStorageData;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ItemStackHelper;

import ic2.api.item.IC2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitEnergy extends ItemKitBase {

	public ItemKitEnergy() {
		super(ItemCardType.KIT_ENERGY, "kit_energy");
		//addRecipe(new Object[] { "RF", "PO", 'P', Items.PAPER, 'R', "dustRedstone", 'F', IC2Items.getItem("frequency_transmitter"), 'O', "dyeRed" });
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		EnergyStorageData storage = EnergyCardHelper.getStorageAt(world, pos, EnergyStorageData.TARGET_TYPE_UNKNOWN);
		if (storage == null)
			return ItemStack.EMPTY;

		ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
		ItemStackHelper.setCoordinates(sensorLocationCard, pos, storage.type);
		return sensorLocationCard;
	}
}
