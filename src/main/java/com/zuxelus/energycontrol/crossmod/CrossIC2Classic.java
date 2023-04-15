package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardIC2;
import com.zuxelus.energycontrol.items.kits.ItemKitIC2;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.tiles.readers.IEUProducer;
import ic2.api.tiles.readers.IEUStorage;
import ic2.api.util.DirectionList;
import ic2.core.block.generators.tiles.FuelGenTileEntity;
import ic2.core.block.generators.tiles.GeoGenTileEntity;
import ic2.core.block.generators.tiles.LiquidFuelGenTileEntity;
import ic2.core.block.generators.tiles.SolarPanelTileEntity;
import ic2.core.block.generators.tiles.ThermalGeneratorTileEntity;
import ic2.core.block.generators.tiles.WaterMillTileEntity;
import ic2.core.block.generators.tiles.WaveGenTileEntity;
import ic2.core.block.generators.tiles.WindTurbineTileEntity;
import ic2.core.block.machines.tiles.mv.RefineryTileEntity;
import ic2.core.platform.wind.WindManager;
import ic2.core.utils.helpers.AABBUtil;
import ic2.core.utils.math.geometry.Box;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class CrossIC2Classic extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		tag.putString(DataHelper.EUTYPE, "EU");
		if (te instanceof IEUStorage) {
			tag.putDouble(DataHelper.ENERGY, ((IEUStorage) te).getStoredEU());
			tag.putDouble(DataHelper.CAPACITY, ((IEUStorage) te).getMaxEU());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		if (te instanceof IEUStorage) {
			tag.putDouble(DataHelper.ENERGY, ((IEUStorage) te).getStoredEU());
			tag.putDouble(DataHelper.CAPACITY, ((IEUStorage) te).getMaxEU());
			if (te instanceof IEUProducer) {
				tag.putDouble(DataHelper.OUTPUT, ((IEUProducer) te).getEUProduction());
			}
			if (te instanceof FuelGenTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((FuelGenTileEntity) te).fuel > 0);
				tag.putInt(DataHelper.FUEL, ((FuelGenTileEntity) te).fuel);
			}
			if (te instanceof GeoGenTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((GeoGenTileEntity) te).fuel > 0);
				tag.putInt(DataHelper.FUEL, ((GeoGenTileEntity) te).fuel);
			}
			
			if (te instanceof WindTurbineTileEntity) {
				tag.putInt("obstructedBlocks", 525 - AABBUtil.countBlocks(te.getLevel(), te.getBlockPos(), (new Box(-4, -2, -4, 4, 4, 4)).offset(te.getBlockPos()), ((WindTurbineTileEntity) te).FILTER, 0, DirectionList.ALL, 525));
				tag.putDouble("wind", 16.0D / 100.0D * Math.abs(WindManager.INSTANCE.getAirSpeed(te.getLevel(), (new AABB(-4.0D, -2.0D, -4.0D, 4.0D, 4.0D, 4.0D)).move(te.getBlockPos()), ((WindTurbineTileEntity) te).getFacing().toYRot(), 90.0F)));
			}
			if (te instanceof WaterMillTileEntity) {
				tag.putInt(DataHelper.FUEL, ((WaterMillTileEntity) te).fuel);
			}
			if (te instanceof LiquidFuelGenTileEntity) {
				tag.putInt(DataHelper.FUEL, ((LiquidFuelGenTileEntity) te).fuel);
				FluidInfo.addTank(DataHelper.TANK, tag, ((LiquidFuelGenTileEntity) te).tank);
			}
			if (te instanceof ThermalGeneratorTileEntity) {
				tag.putBoolean(DataHelper.ACTIVE, ((ThermalGeneratorTileEntity) te).isActive());
				if (!((ThermalGeneratorTileEntity) te).isActive())
					tag.putDouble(DataHelper.OUTPUT, 0);
				tag.putInt(DataHelper.FUEL, ((ThermalGeneratorTileEntity) te).getFuel());
			}
			if (te instanceof RefineryTileEntity) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((RefineryTileEntity) te).firstTank);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((RefineryTileEntity) te).secondTank);
				FluidInfo.addTank(DataHelper.TANK3, tag, ((RefineryTileEntity) te).output);
			}
			return tag;
		}
		if (te instanceof SolarPanelTileEntity) {
			tag.putBoolean(DataHelper.ACTIVE, ((SolarPanelTileEntity) te).isActive());
			tag.putDouble(DataHelper.OUTPUT, ((SolarPanelTileEntity) te).isActive() ? ((SolarPanelTileEntity) te).getEUProduction() : 0);
			return tag;
		}
		if (te instanceof WaveGenTileEntity) {
			tag.putDouble(DataHelper.ENERGY, ((WaveGenTileEntity) te).getProvidedEnergy());
			tag.putDouble(DataHelper.CAPACITY, ((WaveGenTileEntity) te).getMaxEnergyOutput());
			tag.putDouble(DataHelper.OUTPUT, ((WaveGenTileEntity) te).getEUProduction());
			return tag;
		}
		return null;
	}

	@Override
	public void registerItems(RegisterHelper<Item> event) {
		ModItems.kit_ic2 = new ItemKitIC2();
		event.register("kit_ic2", ModItems.kit_ic2);
		ModItems.card_ic2 = new ItemCardIC2();
		event.register("card_ic2", ModItems.card_ic2);
	}
}
