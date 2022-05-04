package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitGalacticraft extends ItemKitBase {

	public ItemKitGalacticraft() {
		super(ItemCardType.KIT_GALACTICRAFT, "kit_galacticraft");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityOxygenCollector || te instanceof TileEntityOxygenSealer
				|| te instanceof TileEntityOxygenDetector || te instanceof TileEntityRefinery
				|| te instanceof TileEntityElectrolyzer || te instanceof TileEntityMethaneSynthesizer
				|| te instanceof TileEntityGasLiquefier || te instanceof TileEntityOxygenStorageModule
				|| te instanceof TileEntityEnergyStorageModule || te instanceof TileEntitySolar
				|| te instanceof TileEntityLaunchController) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GALACTICRAFT);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}
}