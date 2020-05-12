package com.zuxelus.energycontrol.items.kits;

import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCoreStabilizer;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitDraconic extends ItemKitBase {

	public ItemKitDraconic() {
		super(ItemCardType.KIT_DRACONIC, "kit_draconic");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		TileEnergyStorageCore core = null;
		if (te instanceof TileEnergyCoreStabilizer)
			core = ((TileEnergyCoreStabilizer)te).getCore();
		if (te instanceof TileEnergyStorageCore)
			core = (TileEnergyStorageCore)te;
		if (core != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, core.getPos());
			return sensorLocationCard;
		}
		if (te instanceof TileEnergyInfuser || te instanceof TileCrystalDirectIO) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, te.getPos());
			return sensorLocationCard;
		}
		TileReactorCore reactor = null;
		if (te instanceof TileReactorStabilizer) {
			reactor = ((TileReactorStabilizer)te).tryGetCore();
		}
		if (reactor != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, reactor.getPos());
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}
}
