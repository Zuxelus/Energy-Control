package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.BlockDummyable;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.items.machine.ItemBattery;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.TileEntityMachineGasCent.PseudoFluidTank;
import com.hbm.tileentity.machine.fusion.*;
import com.hbm.tileentity.machine.oil.*;
import com.hbm.tileentity.machine.rbmk.*;
import com.hbm.tileentity.machine.storage.*;
import com.zuxelus.energycontrol.hooks.HBMHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardHBM;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitHBM;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import api.hbm.fluidmk2.IFluidUserMK2;
import api.hbm.tile.IInfoProviderEC;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class CrossHBM extends CrossModBase {

	@Override
	public boolean isElectricItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemBattery)
			return true;
		return false;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed) {
		ItemBattery item = (ItemBattery) stack.getItem();
		long amount =  Math.min(Math.min((long) needed, item.getDischargeRate(null)), item.getCharge(stack));
		item.dischargeBattery(stack, amount);
		return amount;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound data = getCardData(te);
		if (data == null || !data.hasKey(DataHelper.ENERGY))
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "HE");
		tag.setDouble(DataHelper.ENERGY, data.getLong(DataHelper.ENERGY));
		tag.setDouble(DataHelper.CAPACITY, data.getLong(DataHelper.CAPACITY));
		return tag;
	}

	@Override
	public int getHeat(World world, int x, int y, int z) {
		if (world == null)
			return -1;

		int t = -1;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			t = getHeat(te);
			if (t > 0)
				return t;
		}
		for (int xoffset = -3; xoffset < 4; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -3; zoffset < 4; zoffset++) {
					TileEntity te = world.getTileEntity(x + xoffset, y + yoffset, z + zoffset);
					t = getHeat(te);
					if (t > 0)
						return t;
				}
		return t;
	}

	private int getHeat(TileEntity te) {
		if (te instanceof TileEntityRBMKBase)
			return (int) ((TileEntityRBMKBase) te).heat;
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityProxyCombo) {
			te = ((TileEntityProxyCombo) te).getTile();
		}
		if (te instanceof TileEntityDummy) {
			te = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
		}
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof IFluidUserMK2) {
			FluidTank[] list = ((IFluidUserMK2) te).getAllTanks();
			if (list.length == 0)
				return null;
			for (FluidTank tank : list)
				result.add(toFluidInfo(tank));
			return result;
		}
		return null;
	}

	private static FluidInfo toFluidInfo(FluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private static FluidInfo toFluidInfo(PseudoFluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private void addTank(String name, NBTTagCompound tag, FluidTank tank) {
		if (tank == null || tank.getFill() == 0)
			tag.setString(name, "N/A");
		else {
			String fluidName = tank.getTankType().getUnlocalizedName();
			if (!StatCollector.translateToLocal(fluidName).equals(fluidName))
				tag.setString(name, String.format("%s: %s mB", StatCollector.translateToLocal(fluidName), tank.getFill()));
			else
				tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
		}
	}

	private void addTank(String name, NBTTagCompound tag, PseudoFluidTank tank) {
		if (tank == null || tank.getFill() == 0)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
	}

	public TileEntity findTileEntity(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block instanceof BlockDummyable) {
			int[] pos = ((BlockDummyable) block).findCore(world, x, y, z);
			return world.getTileEntity(pos[0], pos[1], pos[2]);
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityProxyCombo)
			te = ((TileEntityProxyCombo) te).getTile();
		if (te instanceof TileEntityDummy)
			te = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);

		// Batteries keep their own trending (DIFF) behavior and return early, same as before.
		if (te instanceof TileEntityMachineBattery) {
			TileEntityMachineBattery m = (TileEntityMachineBattery) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}

		NBTTagCompound tag = new NBTTagCompound();
		boolean found = false;

		if (te instanceof IInfoProviderEC) {
			((IInfoProviderEC) te).provideExtraInfo(tag);
			found = true;
		}

		// ---------------- Storage ----------------
		if (te instanceof TileEntityBatteryREDD) {
			TileEntityBatteryREDD m = (TileEntityBatteryREDD) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityBatterySocket) {
			TileEntityBatterySocket m = (TileEntityBatterySocket) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineFluidTank) {
			addTank(DataHelper.TANK, tag, ((TileEntityMachineFluidTank) te).tank);
			found = true;
		} else if (te instanceof TileEntityBarrel) { // also covers TileEntityMachineBAT9000 and TileEntityMachineOrbus
			addTank(DataHelper.TANK, tag, ((TileEntityBarrel) te).tank);
			found = true;

		// ---------------- RTGs / radioisotope ----------------
		} else if (te instanceof TileEntityMachineRTG) {
			TileEntityMachineRTG m = (TileEntityMachineRTG) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineMiniRTG) {
			TileEntityMachineMiniRTG m = (TileEntityMachineMiniRTG) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineRadGen) {
			TileEntityMachineRadGen m = (TileEntityMachineRadGen) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineRadGen.maxPower);
			found = true;
		} else if (te instanceof TileEntityMachineRadiolysis) {
			TileEntityMachineRadiolysis m = (TileEntityMachineRadiolysis) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;

		// ---------------- Turbines / engines ----------------
		} else if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine m = (TileEntityMachineTurbine) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineTurbine.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setBoolean(DataHelper.ACTIVE, (Long) values.get(1) > 0);
				tag.setDouble(DataHelper.CONSUMPTION, (Long) values.get(0));
				tag.setDouble(DataHelper.OUTPUTMB, (Long) values.get(1));
				tag.setDouble(DataHelper.OUTPUT, (Long) values.get(2));
			}
			found = true;
		} else if (te instanceof TileEntityMachineLargeTurbine) {
			TileEntityMachineLargeTurbine m = (TileEntityMachineLargeTurbine) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineLargeTurbine.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setBoolean(DataHelper.ACTIVE, (Long) values.get(1) > 0);
				tag.setDouble(DataHelper.CONSUMPTION, (Long) values.get(0));
				tag.setDouble(DataHelper.OUTPUTMB, (Long) values.get(1));
				tag.setDouble(DataHelper.OUTPUT, (Long) values.get(2));
			}
			found = true;
		} else if (te instanceof TileEntityChungus) {
			TileEntityChungus m = (TileEntityChungus) te;
			tag.setLong(DataHelper.ENERGY, m.powerBuffer);
			tag.setLong(DataHelper.CAPACITY, m.powerBuffer);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setBoolean(DataHelper.ACTIVE, (Long) values.get(1) > 0);
				tag.setDouble(DataHelper.CONSUMPTION, (Long) values.get(0));
				tag.setDouble(DataHelper.OUTPUTMB, (Long) values.get(1));
				tag.setDouble(DataHelper.OUTPUT, (Long) values.get(2));
			}
			found = true;
		} else if (te instanceof TileEntityMachineIndustrialTurbine) {
			TileEntityMachineIndustrialTurbine m = (TileEntityMachineIndustrialTurbine) te;
			tag.setLong(DataHelper.ENERGY, m.powerBuffer);
			tag.setLong(DataHelper.CAPACITY, m.powerBuffer);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityMachineTurbineGas) {
			TileEntityMachineTurbineGas m = (TileEntityMachineTurbineGas) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineTurbineGas.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setDouble(DataHelper.CONSUMPTION, (Double) values.get(0));
				tag.setDouble(DataHelper.OUTPUT, (Double) values.get(1));
				tag.setBoolean(DataHelper.ACTIVE, (Double) values.get(1) > 0);
			}
			found = true;
		} else if (te instanceof TileEntitySteamEngine) {
			TileEntitySteamEngine m = (TileEntitySteamEngine) te;
			tag.setLong(DataHelper.ENERGY, m.powerBuffer);
			tag.setLong(DataHelper.CAPACITY, m.powerBuffer);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityStirling) {
			TileEntityStirling m = (TileEntityStirling) te;
			tag.setLong(DataHelper.ENERGY, m.powerBuffer);
			tag.setLong(DataHelper.CAPACITY, m.powerBuffer);
			found = true;
		} else if (te instanceof TileEntityMachineWoodBurner) {
			TileEntityMachineWoodBurner m = (TileEntityMachineWoodBurner) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineWoodBurner.maxPower);
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineDiesel) {
			TileEntityMachineDiesel m = (TileEntityMachineDiesel) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineCombustionEngine) {
			TileEntityMachineCombustionEngine m = (TileEntityMachineCombustionEngine) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineTurbofan) {
			TileEntityMachineTurbofan m = (TileEntityMachineTurbofan) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineTurbofan.maxPower);
			addTank(DataHelper.TANK, tag, m.tank);
			addTank(DataHelper.TANK2, tag, m.blood);
			found = true;
		} else if (te instanceof TileEntityMachineRotaryFurnace) {
			TileEntityMachineRotaryFurnace m = (TileEntityMachineRotaryFurnace) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineShredder) {
			TileEntityMachineShredder m = (TileEntityMachineShredder) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineShredder.maxPower);
			found = true;
		} else if (te instanceof TileEntityMachineCompressorBase) { // covers Compressor and CompressorCompact
			TileEntityMachineCompressorBase m = (TileEntityMachineCompressorBase) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;

		// ---------------- Chemical / processing ----------------
		} else if (te instanceof TileEntityMachineCentrifuge) {
			TileEntityMachineCentrifuge m = (TileEntityMachineCentrifuge) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineGasCent) {
			TileEntityMachineGasCent m = (TileEntityMachineGasCent) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			addTank(DataHelper.TANK2, tag, m.inputTank);
			addTank(DataHelper.TANK3, tag, m.outputTank);
			found = true;
		} else if (te instanceof TileEntityMachineCyclotron) {
			TileEntityMachineCyclotron m = (TileEntityMachineCyclotron) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineElectricFurnace) {
			TileEntityMachineElectricFurnace m = (TileEntityMachineElectricFurnace) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineArcFurnaceLarge) {
			TileEntityMachineArcFurnaceLarge m = (TileEntityMachineArcFurnaceLarge) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineArcWelder) {
			TileEntityMachineArcWelder m = (TileEntityMachineArcWelder) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineAssemblyMachine) {
			TileEntityMachineAssemblyMachine m = (TileEntityMachineAssemblyMachine) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.inputTank);
			addTank(DataHelper.TANK2, tag, m.outputTank);
			found = true;
		} else if (te instanceof TileEntityMachineAssemblyFactory) {
			TileEntityMachineAssemblyFactory m = (TileEntityMachineAssemblyFactory) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.inputTanks[0]);
			addTank(DataHelper.TANK2, tag, m.outputTanks[0]);
			addTank(DataHelper.TANK3, tag, m.water);
			addTank(DataHelper.TANK4, tag, m.lps);
			found = true;
		} else if (te instanceof TileEntityMachineChemicalPlant) {
			TileEntityMachineChemicalPlant m = (TileEntityMachineChemicalPlant) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.inputTanks[0]);
			addTank(DataHelper.TANK2, tag, m.inputTanks[1]);
			addTank(DataHelper.TANK3, tag, m.inputTanks[2]);
			addTank(DataHelper.TANK4, tag, m.outputTanks[0]);
			addTank(DataHelper.TANK5, tag, m.outputTanks[1]);
			found = true;
		} else if (te instanceof TileEntityMachineChemicalFactory) {
			TileEntityMachineChemicalFactory m = (TileEntityMachineChemicalFactory) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.inputTanks[0]);
			addTank(DataHelper.TANK2, tag, m.inputTanks[1]);
			addTank(DataHelper.TANK3, tag, m.inputTanks[2]);
			addTank(DataHelper.TANK4, tag, m.outputTanks[0]);
			addTank(DataHelper.TANK5, tag, m.outputTanks[1]);
			found = true;
		} else if (te instanceof TileEntityMachinePUREX) {
			TileEntityMachinePUREX m = (TileEntityMachinePUREX) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.inputTanks[0]);
			addTank(DataHelper.TANK2, tag, m.inputTanks[1]);
			addTank(DataHelper.TANK3, tag, m.inputTanks[2]);
			addTank(DataHelper.TANK4, tag, m.outputTanks[0]);
			found = true;
		} else if (te instanceof TileEntityMachinePrecAss) {
			TileEntityMachinePrecAss m = (TileEntityMachinePrecAss) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.inputTank);
			addTank(DataHelper.TANK2, tag, m.outputTank);
			found = true;
		} else if (te instanceof TileEntityMachineMixer) {
			TileEntityMachineMixer m = (TileEntityMachineMixer) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineMixer.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineOreSlopper) {
			TileEntityMachineOreSlopper m = (TileEntityMachineOreSlopper) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineOreSlopper.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityMachineCrystallizer) {
			TileEntityMachineCrystallizer m = (TileEntityMachineCrystallizer) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineExcavator) {
			TileEntityMachineExcavator m = (TileEntityMachineExcavator) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineExposureChamber) {
			TileEntityMachineExposureChamber m = (TileEntityMachineExposureChamber) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineDetector) {
			TileEntityMachineDetector m = (TileEntityMachineDetector) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineHephaestus) {
			TileEntityMachineHephaestus m = (TileEntityMachineHephaestus) te;
			addTank(DataHelper.TANK, tag, m.input);
			addTank(DataHelper.TANK2, tag, m.output);
			found = true;
		} else if (te instanceof TileEntityMachineAnnihilator) {
			TileEntityMachineAnnihilator m = (TileEntityMachineAnnihilator) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineAutosaw) {
			TileEntityMachineAutosaw m = (TileEntityMachineAutosaw) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineDrain) {
			TileEntityMachineDrain m = (TileEntityMachineDrain) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineMiningLaser) {
			TileEntityMachineMiningLaser m = (TileEntityMachineMiningLaser) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineMiningLaser.maxPower);
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineStrandCaster) {
			TileEntityMachineStrandCaster m = (TileEntityMachineStrandCaster) te;
			addTank(DataHelper.TANK, tag, m.water);
			addTank(DataHelper.TANK2, tag, m.steam);
			found = true;
		} else if (te instanceof TileEntityMachineSolderingStation) {
			TileEntityMachineSolderingStation m = (TileEntityMachineSolderingStation) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineEPress) {
			TileEntityMachineEPress m = (TileEntityMachineEPress) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineTeleporter) {
			TileEntityMachineTeleporter m = (TileEntityMachineTeleporter) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMicrowave) {
			TileEntityMicrowave m = (TileEntityMicrowave) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityConveyorPress) {
			TileEntityConveyorPress m = (TileEntityConveyorPress) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityForceField) {
			TileEntityForceField m = (TileEntityForceField) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityFEL) {
			TileEntityFEL m = (TileEntityFEL) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityElectrolyser) {
			TileEntityElectrolyser m = (TileEntityElectrolyser) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			found = true;
		} else if (te instanceof TileEntityDeuteriumExtractor) { // also covers TileEntityDeuteriumTower
			TileEntityDeuteriumExtractor m = (TileEntityDeuteriumExtractor) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityCoreStabilizer) {
			TileEntityCoreStabilizer m = (TileEntityCoreStabilizer) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityCoreEmitter) {
			TileEntityCoreEmitter m = (TileEntityCoreEmitter) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityCoreReceiver) {
			TileEntityCoreReceiver m = (TileEntityCoreReceiver) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityCoreInjector) {
			TileEntityCoreInjector m = (TileEntityCoreInjector) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityCore) {
			TileEntityCore m = (TileEntityCore) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityCustomMachine) {
			TileEntityCustomMachine m = (TileEntityCustomMachine) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			if (m.inputTanks != null && m.inputTanks.length > 0)
				addTank(DataHelper.TANK, tag, m.inputTanks[0]);
			if (m.outputTanks != null && m.outputTanks.length > 0)
				addTank(DataHelper.TANK2, tag, m.outputTanks[0]);
			found = true;
		} else if (te instanceof TileEntityCondenserPowered) {
			TileEntityCondenserPowered m = (TileEntityCondenserPowered) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setDouble(DataHelper.OUTPUTMB, (Long) values.get(0));
				tag.setBoolean(DataHelper.ACTIVE, (Long) values.get(0) > 0);
			}
			found = true;
		} else if (te instanceof TileEntityCondenser) { // also covers TileEntityTowerLarge and TileEntityTowerSmall
			TileEntityCondenser m = (TileEntityCondenser) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			ArrayList values = getHookValues(m);
			if (values != null) {
				tag.setDouble(DataHelper.OUTPUTMB, (Long) values.get(0));
				tag.setBoolean(DataHelper.ACTIVE, (Long) values.get(0) > 0);
			}
			found = true;
		} else if (te instanceof TileEntityHeaterElectric) {
			TileEntityHeaterElectric m = (TileEntityHeaterElectric) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityRadiobox) {
			TileEntityRadiobox m = (TileEntityRadiobox) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityTesla) {
			TileEntityTesla m = (TileEntityTesla) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityMachineRadarNT) {
			TileEntityMachineRadarNT m = (TileEntityMachineRadarNT) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineRadarNT.maxPower);
			found = true;
		} else if (te instanceof TileEntityMachineIGenerator) {
			TileEntityMachineIGenerator m = (TileEntityMachineIGenerator) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachineIGenerator.maxPower);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineIntake) {
			TileEntityMachineIntake m = (TileEntityMachineIntake) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.compair);
			found = true;
		} else if (te instanceof TileEntityMachinePumpElectric) {
			TileEntityMachinePumpElectric m = (TileEntityMachinePumpElectric) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, TileEntityMachinePumpElectric.maxPower);
			addTank(DataHelper.TANK, tag, m.water);
			found = true;
		} else if (te instanceof TileEntityMachinePumpSteam) {
			TileEntityMachinePumpSteam m = (TileEntityMachinePumpSteam) te;
			addTank(DataHelper.TANK, tag, m.water);
			addTank(DataHelper.TANK2, tag, m.steam);
			addTank(DataHelper.TANK3, tag, m.lps);
			found = true;
		} else if (te instanceof TileEntityFurnaceCombination) {
			TileEntityFurnaceCombination m = (TileEntityFurnaceCombination) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityHeatBoiler) {
			TileEntityHeatBoiler m = (TileEntityHeatBoiler) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityHeatBoilerIndustrial) {
			TileEntityHeatBoilerIndustrial m = (TileEntityHeatBoilerIndustrial) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityHeaterHeatex) {
			TileEntityHeaterHeatex m = (TileEntityHeaterHeatex) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityHeaterOilburner) {
			TileEntityHeaterOilburner m = (TileEntityHeaterOilburner) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;

		// ---------------- Oil refining chain ----------------
		} else if (te instanceof TileEntityOilDrillBase) { // covers OilWell, Pumpjack and FrackingTower
			TileEntityOilDrillBase m = (TileEntityOilDrillBase) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			if (m.tanks.length > 2)
				addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineRefinery) {
			TileEntityMachineRefinery m = (TileEntityMachineRefinery) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			addTank(DataHelper.TANK5, tag, m.tanks[4]);
			found = true;
		} else if (te instanceof TileEntityMachineVacuumDistill) {
			TileEntityMachineVacuumDistill m = (TileEntityMachineVacuumDistill) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			addTank(DataHelper.TANK5, tag, m.tanks[4]);
			found = true;
		} else if (te instanceof TileEntityMachineCatalyticCracker) {
			TileEntityMachineCatalyticCracker m = (TileEntityMachineCatalyticCracker) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			addTank(DataHelper.TANK5, tag, m.tanks[4]);
			found = true;
		} else if (te instanceof TileEntityMachineCatalyticReformer) {
			TileEntityMachineCatalyticReformer m = (TileEntityMachineCatalyticReformer) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			found = true;
		} else if (te instanceof TileEntityMachineHydrotreater) {
			TileEntityMachineHydrotreater m = (TileEntityMachineHydrotreater) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			found = true;
		} else if (te instanceof TileEntityMachineFractionTower) {
			TileEntityMachineFractionTower m = (TileEntityMachineFractionTower) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityMachineCoker) {
			TileEntityMachineCoker m = (TileEntityMachineCoker) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityMachineGasFlare) {
			TileEntityMachineGasFlare m = (TileEntityMachineGasFlare) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineLiquefactor) {
			TileEntityMachineLiquefactor m = (TileEntityMachineLiquefactor) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachineSolidifier) {
			TileEntityMachineSolidifier m = (TileEntityMachineSolidifier) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityMachinePyroOven) {
			TileEntityMachinePyroOven m = (TileEntityMachinePyroOven) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;

		// ---------------- RBMK reactor parts ----------------
		} else if (te instanceof TileEntityRBMKBoiler) {
			TileEntityRBMKBoiler m = (TileEntityRBMKBoiler) te;
			addTank(DataHelper.TANK, tag, m.feed);
			addTank(DataHelper.TANK2, tag, m.steam);
			found = true;
		} else if (te instanceof TileEntityRBMKHeater) {
			TileEntityRBMKHeater m = (TileEntityRBMKHeater) te;
			addTank(DataHelper.TANK, tag, m.feed);
			addTank(DataHelper.TANK2, tag, m.steam);
			found = true;
		} else if (te instanceof TileEntityRBMKCooler) {
			TileEntityRBMKCooler m = (TileEntityRBMKCooler) te;
			FluidTank[] cooler = m.getAllTanks();
			addTank(DataHelper.TANK, tag, cooler[0]);
			addTank(DataHelper.TANK2, tag, cooler[1]);
			found = true;
		} else if (te instanceof TileEntityRBMKInlet) {
			TileEntityRBMKInlet m = (TileEntityRBMKInlet) te;
			addTank(DataHelper.TANK, tag, m.water);
			found = true;
		} else if (te instanceof TileEntityRBMKOutlet) {
			TileEntityRBMKOutlet m = (TileEntityRBMKOutlet) te;
			addTank(DataHelper.TANK, tag, m.steam);
			found = true;
		} else if (te instanceof TileEntityRBMKOutgasser) {
			TileEntityRBMKOutgasser m = (TileEntityRBMKOutgasser) te;
			addTank(DataHelper.TANK, tag, m.gas);
			found = true;
		} else if (te instanceof TileEntityRBMKControl) { // covers Auto and Manual variants
			TileEntityRBMKControl m = (TileEntityRBMKControl) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;

		// ---------------- Fusion reactor parts ----------------
		} else if (te instanceof TileEntityFusionTorus) {
			TileEntityFusionTorus m = (TileEntityFusionTorus) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			addTank(DataHelper.TANK4, tag, m.tanks[3]);
			addTank(DataHelper.TANK5, tag, m.coolantTanks[1]);
			found = true;
		} else if (te instanceof TileEntityFusionKlystron) {
			TileEntityFusionKlystron m = (TileEntityFusionKlystron) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.compair);
			found = true;
		} else if (te instanceof TileEntityFusionMHDT) {
			TileEntityFusionMHDT m = (TileEntityFusionMHDT) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityFusionPlasmaForge) {
			TileEntityFusionPlasmaForge m = (TileEntityFusionPlasmaForge) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.maxPower);
			addTank(DataHelper.TANK, tag, m.inputTank);
			found = true;
		} else if (te instanceof TileEntityFusionBoiler) {
			TileEntityFusionBoiler m = (TileEntityFusionBoiler) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityFusionBreeder) {
			TileEntityFusionBreeder m = (TileEntityFusionBreeder) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;

		// ---------------- Misc ----------------
		} else if (te instanceof TileEntityICF) {
			TileEntityICF m = (TileEntityICF) te;
			tag.setLong(DataHelper.ENERGY, m.heat);
			tag.setLong(DataHelper.CAPACITY, TileEntityICF.maxHeat);
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		} else if (te instanceof TileEntityICFController) {
			TileEntityICFController m = (TileEntityICFController) te;
			tag.setLong(DataHelper.ENERGY, m.getPower());
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			found = true;
		} else if (te instanceof TileEntityICFPress) {
			TileEntityICFPress m = (TileEntityICFPress) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityReactorZirnox) {
			TileEntityReactorZirnox m = (TileEntityReactorZirnox) te;
			addTank(DataHelper.TANK, tag, m.water);
			addTank(DataHelper.TANK2, tag, m.steam);
			addTank(DataHelper.TANK3, tag, m.carbonDioxide);
			found = true;
		} else if (te instanceof TileEntitySILEX) {
			TileEntitySILEX m = (TileEntitySILEX) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntitySolarBoiler) {
			TileEntitySolarBoiler m = (TileEntitySolarBoiler) te;
			FluidTank[] solar = m.getAllTanks();
			addTank(DataHelper.TANK, tag, solar[0]);
			addTank(DataHelper.TANK2, tag, solar[1]);
			found = true;
		} else if (te instanceof TileEntitySoyuzLauncher) {
			TileEntitySoyuzLauncher m = (TileEntitySoyuzLauncher) te;
			tag.setLong(DataHelper.ENERGY, m.power);
			tag.setLong(DataHelper.CAPACITY, m.getMaxPower());
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityStorageDrum) {
			TileEntityStorageDrum m = (TileEntityStorageDrum) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityRefueler) {
			TileEntityRefueler m = (TileEntityRefueler) te;
			addTank(DataHelper.TANK, tag, m.tank);
			found = true;
		} else if (te instanceof TileEntityPWRController) {
			TileEntityPWRController m = (TileEntityPWRController) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			found = true;
		} else if (te instanceof TileEntityWatz) {
			TileEntityWatz m = (TileEntityWatz) te;
			addTank(DataHelper.TANK, tag, m.tanks[0]);
			addTank(DataHelper.TANK2, tag, m.tanks[1]);
			addTank(DataHelper.TANK3, tag, m.tanks[2]);
			found = true;
		}

		return found ? tag : null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = HBMHooks.map.get(te);
		if (values == null)
			HBMHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitHBM::new);
		ItemCardMain.register(ItemCardHBM::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_HBM,
				new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyeBlack",
					'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', com.hbm.items.ModItems.ingot_steel });

			Recipes.addKitRecipe(ItemCardType.KIT_HBM, ItemCardType.CARD_HBM);
	}

	@Override
	public ResourceLocation getFluidTexture(String fluidName) {
		FluidType type = Fluids.fromName(fluidName);
		return type == null ? null : type.getTexture();
	}
}
