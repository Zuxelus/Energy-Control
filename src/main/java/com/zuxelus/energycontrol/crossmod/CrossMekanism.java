package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.machine.TileEntityMetallurgicInfuser;
import mekanism.common.util.UnitDisplayUtils.EnergyType;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidTank;

public class CrossMekanism extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof TileEntityEnergyCube) {
			CompoundNBT tag = setStorage(((TileEntityEnergyCube) te).getEnergyContainer());
			tag.putInt("type", 12);
			return tag;
		}
		if (te instanceof TileEntityMetallurgicInfuser) {
			CompoundNBT tag = setStorage(((TileEntityMetallurgicInfuser) te).getEnergyContainer());
			tag.putInt("type", 12);
			return tag;
		}
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		List<IFluidTank> result = new ArrayList<>();
		if (te instanceof TileEntityFluidTank) {
			result.add(((TileEntityFluidTank) te).fluidTank);
			return result;
		}
		return null;
	}

	@Override
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof TileEntityMetallurgicInfuser) {
			CompoundNBT tag = setStorage(((TileEntityMetallurgicInfuser) te).getEnergyContainer());
			tag.putInt("type", 1);
			return tag;
		}
		return null;
	}

	public static CompoundNBT setStorage(BasicEnergyContainer container) {
		CompoundNBT tag = new CompoundNBT();
		EnergyType euType = MekanismConfig.general.energyUnit.get();
		tag.putString("euType", euType.name());
		double coef = CrossMekanism.getEUCoef();
		tag.putDouble("storage", container.getEnergy().doubleValue() / coef);
		tag.putDouble("maxStorage", container.getMaxEnergy().doubleValue() / coef);
		return tag;
	}

	public static double getEUCoef() {
		EnergyType euType = MekanismConfig.general.energyUnit.get();
		double coef = 1;
		switch (euType) {
		case EU:
			coef = 10;
			break;
		case FE:
			coef = 2.5;
			break;
		case J:
			coef = 1;
			break;
		}
		return coef;
	}

	public static String getTempString(double temp) {
		switch (MekanismConfig.general.tempUnit.get()) {
		case K:
			return formatTemp(TemperatureUnit.KELVIN, temp);
		case C:
			return formatTemp(TemperatureUnit.CELSIUS, temp);
		case R:
			return formatTemp(TemperatureUnit.RANKINE, temp);
		case F:
			return formatTemp(TemperatureUnit.FAHRENHEIT, temp);
		case STP:
			return formatTemp(TemperatureUnit.AMBIENT, temp);
		default:
			return "";
		}
	}

	private static String formatTemp(TemperatureUnit unit, double temp) {
		return String.format("%.3f %s", unit.convertFromK(temp, true), unit.getSymbol());
	}
}
