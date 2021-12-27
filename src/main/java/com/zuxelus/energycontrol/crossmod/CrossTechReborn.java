package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.powerSystem.PowerSystem;
import reborncore.common.powerSystem.PowerSystem.EnergySystem;
import techreborn.blockentity.generator.BaseFluidGeneratorBlockEntity;
import techreborn.blockentity.storage.fluid.TankUnitBaseBlockEntity;

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
		if (te instanceof TankUnitBaseBlockEntity) {
			TankUnitBaseBlockEntity tank = (TankUnitBaseBlockEntity) te;
			SingleSlotStorage<FluidVariant> storage = tank.getTank();
			if (storage != null) {
			FluidInfo info = new FluidInfo(storage);
			List<FluidInfo> list = new ArrayList<>();
			list.add(info);
			return list;
			}
		}
		if (te instanceof BaseFluidGeneratorBlockEntity ) {
			BaseFluidGeneratorBlockEntity tank = (BaseFluidGeneratorBlockEntity) te;
			SingleSlotStorage<FluidVariant> storage = tank.getTank();
			if (storage != null) {
			FluidInfo info = new FluidInfo(storage);
			List<FluidInfo> list = new ArrayList<>();
			list.add(info);
			return list;
			}
		}
		return null;
	}
}
