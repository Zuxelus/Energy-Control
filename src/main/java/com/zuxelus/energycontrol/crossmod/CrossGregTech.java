package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.SteamMetaTileEntity;
import gregtech.common.metatileentities.steam.boiler.SteamBoiler;
import gregtech.common.metatileentities.steam.boiler.SteamCoalBoiler;
import ic2.core.block.generator.tileentity.TileEntityConversionGenerator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

public class CrossGregTech extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("euType", "HE");
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof MetaTileEntityHolder) {
			List<FluidInfo> result = new ArrayList<>();
			MetaTileEntity base = ((MetaTileEntityHolder) te).getMetaTileEntity();
			try {
				if (base instanceof SteamBoiler) {
					Field field = SteamBoiler.class.getDeclaredField("waterFluidTank");
					field.setAccessible(true);
					result.add(new FluidInfo((FluidTank) field.get(base)));
					field = SteamBoiler.class.getDeclaredField("steamFluidTank");
					field.setAccessible(true);
					result.add(new FluidInfo((FluidTank) field.get(base)));
					return result;
				}
				if (base instanceof SteamMetaTileEntity) {
					Field field = SteamMetaTileEntity.class.getDeclaredField("steamFluidTank");
					field.setAccessible(true);
					result.add(new FluidInfo((FluidTank) field.get(base)));
					return result;
				}
			} catch (Throwable t) { }
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof MetaTileEntityHolder) {
			NBTTagCompound tag = new NBTTagCompound();
			MetaTileEntity base = ((MetaTileEntityHolder) te).getMetaTileEntity();
			try {
				if (base instanceof SteamBoiler) {
					SteamBoiler boiler = (SteamBoiler) base;
					Field field = SteamBoiler.class.getDeclaredField("currentTemperature");
					field.setAccessible(true);
					int currentTemperature = (int) field.get(base);
					field = SteamBoiler.class.getDeclaredField("baseSteamOutput");
					field.setAccessible(true);
					int baseSteamOutput = (int) field.get(base);
					double consumption = 0;
					double output = 0;
					if (currentTemperature >= 100) {
						int rate = boiler.getMaxTemperate() == 1000 ? 10 : 25;
						output = baseSteamOutput * currentTemperature / boiler.getMaxTemperate() * 1.0D / rate * 20;
						consumption = 1.0D / rate * 20;
					}
					tag.setDouble("consumption", consumption);
					tag.setDouble("outputmb", output);
					tag.setInteger("heat", currentTemperature);
					field = SteamBoiler.class.getDeclaredField("fuelBurnTimeLeft");
					field.setAccessible(true);
					int fuelBurnTimeLeft = (int) field.get(base);
					double rate = boiler.getMaxTemperate() == 1000 ? 6 : 12;
					tag.setDouble("burnTime", Math.ceil(fuelBurnTimeLeft * rate / 20.0D));
					field = SteamBoiler.class.getDeclaredField("waterFluidTank");
					field.setAccessible(true);
					FluidInfo.addTank("tank", tag, (FluidTank) field.get(base));
					field = SteamBoiler.class.getDeclaredField("steamFluidTank");
					field.setAccessible(true);
					FluidInfo.addTank("tank2", tag, (FluidTank) field.get(base));
					return tag;
				}
				if (base instanceof SteamMetaTileEntity) {
					Field field = SteamMetaTileEntity.class.getDeclaredField("steamFluidTank");
					field.setAccessible(true);
					FluidInfo.addTank("tank", tag, (FluidTank) field.get(base));
					return tag;
				}
			} catch (Throwable t) { }
		}
		return null;
	}
}
