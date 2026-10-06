package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import reborncore.common.powerSystem.PowerAcceptorBlockEntity;
import reborncore.common.powerSystem.PowerSystem;
import techreborn.blockentity.generator.BaseFluidGeneratorBlockEntity;
import techreborn.blockentity.storage.fluid.TankUnitBaseBlockEntity;

public class CrossTechReborn extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		if (te instanceof PowerAcceptorBlockEntity) {
			CompoundTag tag = new CompoundTag();
			PowerAcceptorBlockEntity storage = (PowerAcceptorBlockEntity) te;
			tag.putInt("type", 12);
			// RebornCore has a single energy unit since 1.20
			tag.putString("euType", PowerSystem.ABBREVIATION);
			tag.putDouble("storage", storage.getEnergy());
			tag.putDouble("maxStorage", storage.getMaxStoredPower());
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
