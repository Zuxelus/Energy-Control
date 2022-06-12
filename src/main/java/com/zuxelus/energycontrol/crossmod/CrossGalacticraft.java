package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.hooks.GalacticraftHooks;
import com.zuxelus.energycontrol.items.cards.ItemCardGalacticraft;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitGalacticraft;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper;
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemAtmosphericValve;
import micdoodle8.mods.galacticraft.planets.mars.tile.*;
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntitySolarArrayController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossGalacticraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "gJ");
		if (te instanceof TileEntityAFSU)
			return null;
		if (te instanceof IEnergyHandlerGC) {
			IEnergyHandlerGC storage = (IEnergyHandlerGC) te;
			tag.setDouble(DataHelper.ENERGY, storage.getEnergyStoredGC(null));
			tag.setDouble(DataHelper.CAPACITY, storage.getMaxEnergyStoredGC(null));
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof IFluidHandlerWrapper) {
			FluidTankInfo[] info = ((IFluidHandlerWrapper) te).getTankInfo(null);
			List<FluidInfo> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidInfo(tank.fluid, tank.capacity));
			if (result.size() > 0)
				return result;
			for (EnumFacing facing : EnumFacing.VALUES) {
				info = ((IFluidHandlerWrapper) te).getTankInfo(facing);
				for (FluidTankInfo tank : info)
					result.add(new FluidInfo(tank.fluid, tank.capacity));
			}
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileEntityOxygenCollector) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntityOxygenCollector) te));
			tag.setDouble("oxygenPerTick", ((TileEntityOxygenCollector) te).lastOxygenCollected * 20);
			tag.setInteger("oxygenStored", ((TileEntityOxygenCollector) te).getOxygenStored());
			tag.setInteger("oxygenCapacity", ((TileEntityOxygenCollector) te).getMaxOxygenStored());
			tag.setDouble(DataHelper.ENERGY, ((TileEntityOxygenCollector) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityOxygenCollector) te).storage.getCapacityGC());
			return tag;
		}
		if (te instanceof TileEntityOxygenSealer) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntityOxygenSealer)te));
			tag.setDouble("oxygenUse", ((TileEntityOxygenSealer) te).oxygenPerTick * 20);
			tag.setString("thermalStatus", GalacticraftHelper.getThermalStatus((TileEntityOxygenSealer)te));
			tag.setInteger("oxygenStored", ((TileEntityOxygenSealer) te).getOxygenStored());
			tag.setInteger("oxygenCapacity", ((TileEntityOxygenSealer) te).getMaxOxygenStored());
			tag.setDouble(DataHelper.ENERGY, ((TileEntityOxygenSealer) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityOxygenSealer) te).storage.getCapacityGC());
			return tag;
		}
		if (te instanceof TileEntityOxygenDetector) {
			IBlockState state = world.getBlockState(pos);
			if (state != null && state.getBlock() instanceof BlockOxygenDetector) {
				tag.setBoolean(DataHelper.ACTIVE, state.getValue(BlockOxygenDetector.ACTIVE));
				tag.setString("oxygenDetected", state.getValue(BlockOxygenDetector.ACTIVE) ? TextFormatting.GREEN + I18n.translateToLocal("msg.ec.InfoPanelTrue") : TextFormatting.RED + I18n.translateToLocal("msg.ec.InfoPanelFalse"));
				return tag;
			}
			return null;
		}
		if (te instanceof TileEntityRefinery) {
			TileEntityRefinery ref = (TileEntityRefinery) te;
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus(ref));
			tag.setDouble(DataHelper.ENERGY, ref.storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ref.storage.getCapacityGC());
			tag.setString("oilTank", String.format("%s / %s mB", ref.oilTank.getFluidAmount(), ref.oilTank.getCapacity()));
			tag.setString("fuelTank", String.format("%s / %s mB", ref.fuelTank.getFluidAmount(), ref.fuelTank.getCapacity()));
			return tag;
		}
		if (te instanceof TileEntityElectrolyzer) {
			TileEntityElectrolyzer el = (TileEntityElectrolyzer) te;
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus(el));
			tag.setDouble(DataHelper.ENERGY, el.storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, el.storage.getCapacityGC());
			tag.setString("waterTank", String.format("%s / %s mB", el.waterTank.getFluidAmount(), el.waterTank.getCapacity()));
			tag.setString("oxygenTank", String.format("%s / %s mB", el.liquidTank.getFluidAmount(), el.liquidTank.getCapacity()));
			tag.setString("hydrogenTank", String.format("%s / %s mB", el.liquidTank2.getFluidAmount(), el.liquidTank2.getCapacity()));
			return tag;
		}
		if (te instanceof TileEntityMethaneSynthesizer) {
			TileEntityMethaneSynthesizer meth = (TileEntityMethaneSynthesizer) te;
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus(meth));
			tag.setDouble(DataHelper.ENERGY, meth.storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, meth.storage.getCapacityGC());
			tag.setString("methaneTank", String.format("%s / %s mB", meth.liquidTank.getFluidAmount(), meth.liquidTank.getCapacity()));
			tag.setString("hydrogenTank", String.format("%s / %s mB", meth.gasTank.getFluidAmount(), meth.gasTank.getCapacity()));
			tag.setString("dioxideTank", String.format("%s / %s mB", meth.gasTank2.getFluidAmount(), meth.gasTank2.getCapacity()));
			int counter = 0;
			ItemStack stack = meth.inventory.get(2);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemAtmosphericValve)
				counter = stack.getCount();
			tag.setInteger("valve", counter);
			counter = 0;
			stack = meth.inventory.get(3);
			if (!stack.isEmpty())
				counter = stack.getCount();
			tag.setInteger("carbon", counter);
			return tag;
		}
		if (te instanceof TileEntityGasLiquefier) {
			TileEntityGasLiquefier gas = (TileEntityGasLiquefier) te;
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus(gas));
			tag.setDouble(DataHelper.ENERGY, gas.storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, gas.storage.getCapacityGC());
			tag.setString("gasTank", String.format("%s / %s mB", gas.gasTank.getFluidAmount(), gas.gasTank.getCapacity()));
			FluidStack liquid = gas.gasTank.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				tag.setString("gasTankName", liquid.getFluid().getUnlocalizedName());
			else
				tag.setString("gasTankName", "");
			tag.setString("liquidTank", String.format("%s / %s mB", gas.liquidTank.getFluidAmount(), gas.liquidTank.getCapacity()));
			liquid = gas.liquidTank.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				tag.setString("liquidTankName", liquid.getFluid().getUnlocalizedName());
			else
				tag.setString("liquidTankName", "");
			tag.setString("liquidTank2", String.format("%s / %s mB", gas.liquidTank2.getFluidAmount(), gas.liquidTank2.getCapacity()));
			liquid = gas.liquidTank2.getFluid(); 
			if (liquid != null && liquid.getFluid() != null)
				tag.setString("liquidTank2Name", liquid.getFluid().getUnlocalizedName());
			else
				tag.setString("liquidTank2Name", "");
			int counter = 0;
			ItemStack stack = gas.inventory.get(1);
			if (!stack.isEmpty() && stack.getItem() instanceof ItemAtmosphericValve)
				counter = stack.getCount();
			tag.setInteger("valve", counter);
			return tag;
		}
		if (te instanceof TileEntityOxygenStorageModule) {
			tag.setInteger("oxygenStored", ((TileEntityOxygenStorageModule) te).getOxygenStored());
			tag.setInteger("oxygenCapacity", ((TileEntityOxygenStorageModule) te).getMaxOxygenStored());
			return tag;
		}
		if (te instanceof TileEntityEnergyStorageModule) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityEnergyStorageModule) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityEnergyStorageModule) te).storage.getCapacityGC());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setDouble(DataHelper.DIFF, ((Double) values.get(0) - (Double) values.get(20)) / 20.0D);
			return tag;
		}
		if (te instanceof TileEntitySolar) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntitySolar) te));
			tag.setDouble(DataHelper.ENERGY, ((TileEntitySolar) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntitySolar) te).storage.getCapacityGC());
			tag.setInteger(DataHelper.OUTPUT, ((TileEntitySolar) te).generateWatts);
			tag.setDouble("boost", Math.round((((TileEntitySolar) te).getSolarBoost() - 1) * 1000) / 10.0D);
			tag.setDouble("sunVisible", Math.round(((TileEntitySolar) te).solarStrength / 9.0F * 1000) / 10.0D);
			return tag;
		}
		if (te instanceof TileEntityLaunchController ) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntityLaunchController) te));
			tag.setDouble(DataHelper.ENERGY, ((TileEntityLaunchController) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityLaunchController) te).storage.getCapacityGC());
			tag.setInteger("frequency", ((TileEntityLaunchController) te).frequency);
			tag.setInteger("target", ((TileEntityLaunchController) te).destFrequency);
			return tag;
		}
		if (te instanceof TileEntitySolarArrayController) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntitySolarArrayController) te));
			tag.setDouble(DataHelper.ENERGY, ((TileEntitySolarArrayController) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntitySolarArrayController) te).storage.getCapacityGC());
			tag.setInteger(DataHelper.OUTPUT, ((TileEntitySolarArrayController) te).generateWatts);
			tag.setDouble("boost", Math.round((((TileEntitySolarArrayController) te).getSolarBoost() - 1) * 1000) / 10.0D);
			//tag.setDouble("sunVisible", Math.round(((TileEntitySolarArrayController) te).solarStrength / 9.0F * 1000) / 10.0D);
			return tag;
		}
		if (te instanceof TileEntityOxygenDistributor) {
			tag.setString(DataHelper.STATUS, GalacticraftHelper.getStatus((TileEntityOxygenDistributor) te));
			tag.setDouble(DataHelper.ENERGY, ((TileEntityOxygenDistributor) te).storage.getEnergyStoredGC());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityOxygenDistributor) te).storage.getCapacityGC());
			tag.setDouble("oxygenUse", ((TileEntityOxygenDistributor) te).oxygenPerTick * 20);
			tag.setInteger("oxygenStored", ((TileEntityOxygenDistributor) te).getOxygenStored());
			tag.setInteger("oxygenCapacity", ((TileEntityOxygenDistributor) te).getMaxOxygenStored());
			return tag;
		}
		if (te instanceof TileEntityCoalGenerator) {
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityCoalGenerator) te).heatGJperTick > 30 ? ((TileEntityCoalGenerator) te).heatGJperTick - 30 : 0);
			return tag;
		}
		return null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = GalacticraftHooks.map.get(te);
		if (values == null)
			GalacticraftHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitGalacticraft::new);
		ItemCardMain.register(ItemCardGalacticraft::new);
	}
}
