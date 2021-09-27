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
		tag.setString("euType", "RF");
		if (te instanceof IExtendedRFStorage) {
			tag.setDouble("maxStorage", (double) ((IExtendedRFStorage)te).getExtendedCapacity());
			tag.setDouble("storage", (double) ((IExtendedRFStorage)te).getExtendedStorage());
			return tag;
		}
		if (te instanceof TileEnergyInfuser) {
			tag.setDouble("maxStorage", ((TileEnergyInfuser) te).energyStorage.getMaxEnergyStored());
			tag.setDouble("storage", ((TileEnergyInfuser) te).energyStorage.getEnergyStored());
			return tag;
		}
		if (te instanceof TileCrystalDirectIO) {
			tag.setDouble("maxStorage", ((TileCrystalDirectIO) te).getMaxEnergyStored());
			tag.setDouble("storage", ((TileCrystalDirectIO) te).getEnergyStored());
			return tag;
		}
		return null;
	}
}
