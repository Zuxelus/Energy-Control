package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;

public class CrossTechReborn extends CrossModBase {

	@Override
	public NbtCompound getEnergyData(BlockEntity te) {
		if (te instanceof PowerAcceptorBlockEntity) {
			NbtCompound tag = new NbtCompound();
			PowerAcceptorBlockEntity storage = (PowerAcceptorBlockEntity) te;
			tag.putInt("type", 12);
			tag.putString("euType", PowerSystem.getDisplayPower().abbreviation);
			if (PowerSystem.getDisplayPower() == EnergySystem.EU) {
				tag.putDouble("storage", storage.getEnergy());
				tag.putDouble("maxStorage", storage.getMaxStoredPower());
			} else {
				tag.putDouble("storage", storage.getEnergy()/* * RebornCoreConfig.euPerFU*/);
				tag.putDouble("maxStorage", storage.getMaxStoredPower()/* * RebornCoreConfig.euPerFU*/);
			}
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}
}
