package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityDynamoElectric;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorSolid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitGregTech extends ItemKitBase {

	public ItemKitGregTech() {
		super(ItemCardType.KIT_GREGTECH, "kit_gregtech");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof MultiTileEntityGeneratorSolid || te instanceof MultiTileEntityBoilerTank
				|| te instanceof MultiTileEntityTurbineSteam || te instanceof MultiTileEntityDynamoElectric) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GREGTECH);
			ItemStackHelper.setCoordinates(sensorLocationCard, x, y, z);
			return sensorLocationCard;
		}
		return null;
	}
}