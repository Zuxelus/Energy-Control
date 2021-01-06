package com.zuxelus.energycontrol.crossmod;

import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import com.brandon3055.draconicevolution.common.tileentities.TileGenerator;
import com.brandon3055.draconicevolution.common.tileentities.energynet.TileEnergyTransceiver;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.TileEnergyStorageCore;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossDraconicEvolution extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileEnergyStorageCore) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("maxStorage", (double) ((TileEnergyStorageCore)te).getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileEnergyStorageCore)te).getEnergyStored());
			return tag;
		}
		if (te instanceof TileEnergyInfuser) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("maxStorage", (double) ((TileEnergyInfuser) te).energy.getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileEnergyInfuser) te).energy.getEnergyStored());
			return tag;
		}
		if (te instanceof TileEnergyTransceiver) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("maxStorage", (double) ((TileEnergyTransceiver) te).getStorage().getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileEnergyTransceiver) te).getStorage().getEnergyStored());
			return tag;
		}
		if (te instanceof TileGenerator) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("type", ItemCardType.EU_RF);
			tag.setDouble("maxStorage", (double) ((TileGenerator) te).storage.getMaxEnergyStored());
			tag.setDouble("storage", (double) ((TileGenerator) te).storage.getEnergyStored());
			return tag;
		}
		return null;
	}
}
