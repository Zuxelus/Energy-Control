package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import nc.tile.energy.battery.TileBattery;
import nc.tile.generator.TileDecayGenerator;
import nc.tile.generator.TileFissionController;
import nc.tile.generator.TileSolarPanel;
import nc.tile.processor.TileFluidProcessor;
import nc.tile.processor.TileItemFluidProcessor;
import nc.tile.processor.TileItemProcessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitNuclearCraft extends ItemKitBase {

	public ItemKitNuclearCraft() {
		super(ItemCardType.KIT_NUCLEARCRAFT, "kit_nuclearcraft");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileDecayGenerator || te instanceof TileSolarPanel || te instanceof TileItemProcessor || te instanceof TileItemFluidProcessor || te instanceof TileBattery || te instanceof TileFluidProcessor || te instanceof TileFissionController) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_NUCLEARCRAFT);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
