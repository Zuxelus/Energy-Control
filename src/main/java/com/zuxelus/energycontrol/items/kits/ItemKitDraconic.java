package com.zuxelus.energycontrol.items.kits;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.TileParticleGenerator;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorCore;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.TileReactorStabilizer;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitDraconic extends ItemKitBase {

	public ItemKitDraconic() {
		super(ItemCardType.KIT_DRACONIC, "kit_draconic");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		TileEnergyStorageCore core = null;
		if (te instanceof TileParticleGenerator)
			core = ((TileParticleGenerator)te).getMaster();
		if (te instanceof TileEnergyStorageCore)
			core = (TileEnergyStorageCore)te;
		if (core != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, core.xCoord, core.yCoord, core.zCoord);
			return sensorLocationCard;
		}
		if (te instanceof TileEnergyInfuser || te instanceof TileEnergyTransceiver || te instanceof TileGenerator) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, te.xCoord, te.yCoord, te.zCoord);
			return sensorLocationCard;
		}
		TileReactorCore reactor = null;
		if (te instanceof TileReactorCore)
			reactor = (TileReactorCore) te;
		if (te instanceof TileReactorStabilizer)
			reactor = (TileReactorCore) ((TileReactorStabilizer)te).masterLocation.getTileEntity(world);
		if (reactor != null) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_REACTOR_DRACONIC);
			ItemStackHelper.setCoordinates(sensorLocationCard, reactor.xCoord, reactor.yCoord, reactor.zCoord);
			return sensorLocationCard;
		}
		return null;
	}
}
