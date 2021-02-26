package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitGalacticraft extends ItemKitBase {

	public ItemKitGalacticraft() {
		super(ItemCardType.KIT_GALACTICRAFT, "kit_galacticraft");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityOxygenCollector || te instanceof TileEntityOxygenSealer
				|| te instanceof TileEntityOxygenDistributor
				|| te instanceof TileEntityOxygenDetector || te instanceof TileEntityRefinery
				|| te instanceof TileEntityElectrolyzer || te instanceof TileEntityMethaneSynthesizer
				|| te instanceof TileEntityGasLiquefier || te instanceof TileEntityOxygenStorageModule
				|| te instanceof TileEntityEnergyStorageModule || te instanceof TileEntitySolar
				|| te instanceof TileEntityLaunchController || te instanceof TileEntitySolarArrayController) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_GALACTICRAFT);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}