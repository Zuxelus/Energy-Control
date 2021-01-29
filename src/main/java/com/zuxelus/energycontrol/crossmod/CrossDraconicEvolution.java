package com.zuxelus.energycontrol.crossmod;

import com.brandon3055.draconicevolution.api.IExtendedRFStorage;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyInfuser;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossDraconicEvolution extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te == null)
			return null;

		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", ItemCardType.EU_RF);
		if (te instanceof IExtendedRFStorage) {
			tag.setDouble("maxStorage", (double) ((IExtendedRFStorage)te).getExtendedCapacity());
			tag.setDouble("storage", (double) ((IExtendedRFStorage)te).getExtendedStorage());
			return tag;
		}
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble("maxStorage", (double) ((TileEnergyInfuser) te).energyStorage.getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileEnergyInfuser) te).energyStorage.getEnergyStored());
			return tag;
		}
		if (te instanceof TileCrystalDirectIO) {
			tag.setDouble("maxStorage", (double) ((TileCrystalDirectIO) te).getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileCrystalDirectIO) te).getEnergyStored());
			return tag;
		}
		return null;
	}
}
