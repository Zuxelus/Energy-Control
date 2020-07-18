package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import appeng.api.AEApi;
import appeng.api.networking.energy.IAEPowerStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrossAppEng extends CrossModBase {

	public CrossAppEng() {
		super("appliedenergistics2", null, null);
	}

	public NBTTagCompound getEnergyData(TileEntity te) {
		if (!modLoaded)
			return null;

		if (te instanceof IAEPowerStorage) {
			NBTTagCompound tag = new NBTTagCompound();
			IAEPowerStorage storage = (IAEPowerStorage) te;
			tag.setInteger("type", 10);
			tag.setDouble("storage", storage.getAECurrentPower());
			tag.setDouble("maxStorage", storage.getAEMaxPower());
			return tag;
		}
		return null;
	}

	public ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IAEPowerStorage) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENERGY);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return null;
	}

	public ItemStack getItemStack(String name) {
		if (!modLoaded)
			return null;

		switch (name) {
		case "fluixCrystal":
			return AEApi.instance().definitions().materials().fluixCrystal().maybeStack(1).get();
		}
		return null;
	}
}
