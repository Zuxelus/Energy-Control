package com.zuxelus.energycontrol.crossmod;

import appeng.api.networking.energy.IAEPowerStorage;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class CrossAppEng extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof IAEPowerStorage) {
			CompoundNBT tag = new CompoundNBT();
			IAEPowerStorage storage = (IAEPowerStorage) te;
			tag.putInt("type", ItemCardType.EU_AE);
			tag.putDouble("storage", storage.getAECurrentPower());
			tag.putDouble("maxStorage", storage.getAEMaxPower());
			return tag;
		}
		return null;
	}
}
