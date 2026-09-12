package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.CompatibilityConfig;
import com.hbm.inventory.fluid.tank.FluidTankNTM;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.albion.*;
import com.hbm.tileentity.machine.fusion.*;
import com.hbm.tileentity.machine.oil.*;
import com.hbm.tileentity.machine.pile.*;
import com.hbm.tileentity.machine.rbmk.*;
import com.hbm.tileentity.machine.storage.*;
import com.hbm.util.ContaminationUtil;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
//import com.zuxelus.energycontrol.hooks.HBMHooks;
import com.zuxelus.energycontrol.items.cards.ItemCardHBM;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitHBM;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;

public class CrossHBMCE extends CrossModBase {

	@Override
	public boolean isElectricItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemBattery)
			return true;
		return false;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed, int tier) {
		ItemBattery item = (ItemBattery) stack.getItem();
		long amount = Math.min(Math.min((long) needed, item.getDischargeRate(stack)), item.getCharge(stack));
		item.dischargeBattery(stack, amount);
		return amount;
	}

	private TileEntity getCore(TileEntity te) {
		if (te instanceof TileEntityProxyCombo)
			return ((TileEntityProxyCombo) te).getTile();
		if (te instanceof TileEntityDummy)
			return te.getWorld().getTileEntity(((TileEntityDummy) te).target);
		return te;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		te = getCore(te);
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "HE");
		if (te instanceof TileEntityMachineBattery) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityBatteryBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityBatteryBase) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityBatteryBase) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineShredder) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineShredder) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCentrifuge) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCentrifuge) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasCent) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasCent) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineLargeTurbine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCrystallizer) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityChungus) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityChungus) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityChungus) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineIndustrialTurbine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineIndustrialTurbine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineIndustrialTurbine) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineChemicalPlant) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemicalPlant) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemicalPlant) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineChemicalFactory) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemicalFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemicalFactory) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineAssemblyMachine) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssemblyMachine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssemblyMachine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineAssemblyFactory) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssemblyFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssemblyFactory) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbofan) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbofan) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRadGen) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadGen) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadGen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasFlare) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasFlare) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineOilWell) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineOilWell) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineOilWell) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachinePumpjack) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePumpjack) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineFrackingTower) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineFrackingTower) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineFrackingTower) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineRefinery) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRefinery) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRefinery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCatalyticReformer) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCatalyticReformer) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCatalyticReformer) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineHydrotreater) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineHydrotreater) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineHydrotreater) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineVacuumDistill) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineVacuumDistill) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineVacuumDistill) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineLiquefactor) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineLiquefactor) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineLiquefactor) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSolidifier) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSolidifier) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSolidifier) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachinePyroOven) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePyroOven) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePyroOven) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnaceLarge) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineArcFurnaceLarge) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineArcFurnaceLarge) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineArcWelder) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineArcWelder) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineArcWelder) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSolderingStation) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSolderingStation) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSolderingStation) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachinePrecAss) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePrecAss) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePrecAss) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSuperComputer) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSuperComputer) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSuperComputer) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachinePUREX) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePUREX) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePUREX) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineRockMill) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRockMill) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRockMill) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineOreSlopper) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineOreSlopper) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineOreSlopper) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineMixer) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMixer) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMixer) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCompressorBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCompressorBase) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCompressorBase) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCyclotron) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCyclotron) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCyclotron) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineExposureChamber) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineExposureChamber) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineExposureChamber) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineRadiolysis) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadiolysis) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadiolysis) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineElectricFurnace) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineElectricFurnace) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineElectricFurnace) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineEPress) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityConveyorPress) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityConveyorPress) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityConveyorPress) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineAutocrafter) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAutocrafter) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAutocrafter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineExcavator) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineExcavator) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineExcavator) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineMiningLaser) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMiningLaser) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMiningLaser) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineWoodBurner) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineWoodBurner) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineWoodBurner) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCombustionEngine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCombustionEngine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCombustionEngine) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTurbineGas) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbineGas) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbineGas) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSteamEngine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSteamEngine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSteamEngine) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityStirling) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityStirling) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityStirling) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCondenserPowered) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCondenserPowered) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCondenserPowered) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityElectrolyser) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityElectrolyser) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityElectrolyser) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityDeuteriumExtractor) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityDeuteriumExtractor) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineIntake) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineIntake) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineIntake) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachinePumpElectric) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePumpElectric) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePumpElectric) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityHeaterElectric) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityHeaterElectric) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityHeaterElectric) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCharger) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCharger) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCharger) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMicrowave) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMicrowave) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMicrowave) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTeleporter) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTeleporter) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTeleporter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineRadarNT) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadarNT) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadarNT) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityForceField) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityForceField) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityForceField) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityLaunchpadSoyuz) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityLaunchpadSoyuz) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityLaunchpadSoyuz) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntitySoyuzLauncher) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntitySoyuzLauncher) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntitySoyuzLauncher) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFEL) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFEL) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFEL) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityICFController) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityICFController) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityICFController) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCoreEmitter) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCoreEmitter) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCoreEmitter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCoreReceiver) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCoreReceiver) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCoreReceiver) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCoreStabilizer) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCoreStabilizer) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCoreStabilizer) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFusionKlystron) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionKlystron) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionKlystron) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFusionPlasmaForge) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionPlasmaForge) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionPlasmaForge) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFusionMHDT) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionMHDT) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionMHDT) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFusionTorus) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionTorus) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionTorus) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCooledBase) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityCooledBase) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityCooledBase) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityRBMKControl) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityRBMKControl) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityRBMKControl) te).getMaxPower());
			return tag;
		}
		return null;
	}

	@Override
	public int getHeat(World world, BlockPos pos) {
		if (world == null)
			return -1;

		int t = -1;
		for (EnumFacing dir : EnumFacing.VALUES) {
			TileEntity te = world.getTileEntity(pos.offset(dir));
			t = getHeat(te);
			if (t > 0)
				return t;
		}
		for (int xoffset = -3; xoffset < 4; xoffset++)
			for (int yoffset = -1; yoffset < 2; yoffset++)
				for (int zoffset = -3; zoffset < 4; zoffset++) {
					TileEntity te = world.getTileEntity(pos.east(xoffset).up(yoffset).south(zoffset));
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
		te = getCore(te);
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityMachineDiesel) {
			result.add(new FluidInfo(((TileEntityMachineDiesel) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineTurbine) {
			result.add(new FluidInfo(((TileEntityMachineTurbine) te).tanksNew[0]));
			result.add(new FluidInfo(((TileEntityMachineTurbine) te).tanksNew[1]));
			return result;
		}
		if (te instanceof TileEntityMachineGasCent) {
			result.add(new FluidInfo(((TileEntityMachineGasCent) te).tank));
			return result;
		}
		if (te instanceof TileEntityBarrel) {
			result.add(new FluidInfo(((TileEntityBarrel) te).tankNew));
			return result;
		}
		if (te instanceof TileEntityMachineLargeTurbine) {
			result.add(new FluidInfo(((TileEntityMachineLargeTurbine) te).tanksNew[0]));
			result.add(new FluidInfo(((TileEntityMachineLargeTurbine) te).tanksNew[1]));
			return result;
		}
		if (te instanceof TileEntitySolarBoiler) {
			result.add(new FluidInfo(((TileEntitySolarBoiler) te).tanks[0]));
			result.add(new FluidInfo(((TileEntitySolarBoiler) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineFractionTower) {
			result.add(new FluidInfo(((TileEntityMachineFractionTower) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineFractionTower) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineFractionTower) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineCrystallizer) {
			result.add(new FluidInfo(((TileEntityMachineCrystallizer) te).tankNew));
			return result;
		}
		if (te instanceof TileEntityMachineChemicalPlant) {
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).inputTanks[0]));
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).inputTanks[1]));
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).inputTanks[2]));
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).outputTanks[0]));
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).outputTanks[1]));
			result.add(new FluidInfo(((TileEntityMachineChemicalPlant) te).outputTanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineChemicalFactory) {
			TileEntityMachineChemicalFactory factory = (TileEntityMachineChemicalFactory) te;
			for (FluidTankNTM tank : factory.inputTanks)
				result.add(new FluidInfo(tank));
			for (FluidTankNTM tank : factory.outputTanks)
				result.add(new FluidInfo(tank));
			result.add(new FluidInfo(factory.water));
			result.add(new FluidInfo(factory.lps));
			return result;
		}
		if (te instanceof TileEntityMachineAssemblyMachine) {
			result.add(new FluidInfo(((TileEntityMachineAssemblyMachine) te).inputTank));
			result.add(new FluidInfo(((TileEntityMachineAssemblyMachine) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityMachineAssemblyFactory) {
			TileEntityMachineAssemblyFactory factory = (TileEntityMachineAssemblyFactory) te;
			for (FluidTankNTM tank : factory.inputTanks)
				result.add(new FluidInfo(tank));
			for (FluidTankNTM tank : factory.outputTanks)
				result.add(new FluidInfo(tank));
			result.add(new FluidInfo(factory.water));
			result.add(new FluidInfo(factory.lps));
			return result;
		}
		if (te instanceof TileEntityMachinePUREX) {
			result.add(new FluidInfo(((TileEntityMachinePUREX) te).inputTanks[0]));
			result.add(new FluidInfo(((TileEntityMachinePUREX) te).inputTanks[1]));
			result.add(new FluidInfo(((TileEntityMachinePUREX) te).inputTanks[2]));
			result.add(new FluidInfo(((TileEntityMachinePUREX) te).outputTanks[0]));
			return result;
		}
		if (te instanceof TileEntityMachineRockMill) {
			result.add(new FluidInfo(((TileEntityMachineRockMill) te).inputTanks[0]));
			result.add(new FluidInfo(((TileEntityMachineRockMill) te).outputTanks[0]));
			return result;
		}
		if (te instanceof TileEntityMachinePrecAss) {
			result.add(new FluidInfo(((TileEntityMachinePrecAss) te).inputTank));
			result.add(new FluidInfo(((TileEntityMachinePrecAss) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityMachineSuperComputer) {
			result.add(new FluidInfo(((TileEntityMachineSuperComputer) te).inputTank));
			result.add(new FluidInfo(((TileEntityMachineSuperComputer) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityMachineFluidTank) {
			result.add(new FluidInfo(((TileEntityMachineFluidTank) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineGasFlare) {
			result.add(new FluidInfo(((TileEntityMachineGasFlare) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineTurbofan) {
			result.add(new FluidInfo(((TileEntityMachineTurbofan) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineOilWell) {
			result.add(new FluidInfo(((TileEntityMachineOilWell) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineOilWell) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachinePumpjack) {
			result.add(new FluidInfo(((TileEntityMachinePumpjack) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachinePumpjack) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineFrackingTower) {
			result.add(new FluidInfo(((TileEntityMachineFrackingTower) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineFrackingTower) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineFrackingTower) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineRefinery) {
			result.add(new FluidInfo(((TileEntityMachineRefinery) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineRefinery) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineRefinery) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineRefinery) te).tanks[3]));
			result.add(new FluidInfo(((TileEntityMachineRefinery) te).tanks[4]));
			return result;
		}
		if (te instanceof TileEntityMachineCatalyticCracker) {
			result.add(new FluidInfo(((TileEntityMachineCatalyticCracker) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticCracker) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticCracker) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticCracker) te).tanks[3]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticCracker) te).tanks[4]));
			return result;
		}
		if (te instanceof TileEntityMachineCatalyticReformer) {
			result.add(new FluidInfo(((TileEntityMachineCatalyticReformer) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticReformer) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticReformer) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineCatalyticReformer) te).tanks[3]));
			return result;
		}
		if (te instanceof TileEntityMachineHydrotreater) {
			result.add(new FluidInfo(((TileEntityMachineHydrotreater) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineHydrotreater) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineHydrotreater) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineHydrotreater) te).tanks[3]));
			return result;
		}
		if (te instanceof TileEntityMachineVacuumDistill) {
			result.add(new FluidInfo(((TileEntityMachineVacuumDistill) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineVacuumDistill) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineVacuumDistill) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineVacuumDistill) te).tanks[3]));
			result.add(new FluidInfo(((TileEntityMachineVacuumDistill) te).tanks[4]));
			return result;
		}
		if (te instanceof TileEntityMachineCoker) {
			result.add(new FluidInfo(((TileEntityMachineCoker) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineCoker) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachinePyroOven) {
			result.add(new FluidInfo(((TileEntityMachinePyroOven) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachinePyroOven) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineLiquefactor) {
			result.add(new FluidInfo(((TileEntityMachineLiquefactor) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineSolidifier) {
			result.add(new FluidInfo(((TileEntityMachineSolidifier) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineUF6Tank) {
			result.add(new FluidInfo(((TileEntityMachineUF6Tank) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachinePuF6Tank) {
			result.add(new FluidInfo(((TileEntityMachinePuF6Tank) te).tank));
			return result;
		}
		if (te instanceof TileEntityCondenser) {
			result.add(new FluidInfo(((TileEntityCondenser) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityCondenser) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityHeatBoiler) {
			result.add(new FluidInfo(((TileEntityHeatBoiler) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityHeatBoiler) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityHeatBoilerIndustrial) {
			result.add(new FluidInfo(((TileEntityHeatBoilerIndustrial) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityHeatBoilerIndustrial) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityHeaterHeatex) {
			result.add(new FluidInfo(((TileEntityHeaterHeatex) te).tanksNew[0]));
			result.add(new FluidInfo(((TileEntityHeaterHeatex) te).tanksNew[1]));
			return result;
		}
		if (te instanceof TileEntityHeaterOilburner) {
			result.add(new FluidInfo(((TileEntityHeaterOilburner) te).tank));
			return result;
		}
		if (te instanceof TileEntityFurnaceCombination) {
			result.add(new FluidInfo(((TileEntityFurnaceCombination) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineBlastFurnace) {
			result.add(new FluidInfo(((TileEntityMachineBlastFurnace) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineBlastFurnace) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineRotaryFurnace) {
			result.add(new FluidInfo(((TileEntityMachineRotaryFurnace) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineRotaryFurnace) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineRotaryFurnace) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineStrandCaster) {
			result.add(new FluidInfo(((TileEntityMachineStrandCaster) te).water));
			result.add(new FluidInfo(((TileEntityMachineStrandCaster) te).steam));
			return result;
		}
		if (te instanceof TileEntityMachineHephaestus) {
			result.add(new FluidInfo(((TileEntityMachineHephaestus) te).input));
			result.add(new FluidInfo(((TileEntityMachineHephaestus) te).output));
			return result;
		}
		if (te instanceof TileEntityElectrolyser) {
			result.add(new FluidInfo(((TileEntityElectrolyser) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityElectrolyser) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityElectrolyser) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityElectrolyser) te).tanks[3]));
			return result;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			result.add(new FluidInfo(((TileEntityDeuteriumExtractor) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityDeuteriumExtractor) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineMixer) {
			result.add(new FluidInfo(((TileEntityMachineMixer) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineMixer) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineMixer) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineOreSlopper) {
			result.add(new FluidInfo(((TileEntityMachineOreSlopper) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineOreSlopper) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineCompressorBase) {
			result.add(new FluidInfo(((TileEntityMachineCompressorBase) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineCompressorBase) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineCyclotron) {
			result.add(new FluidInfo(((TileEntityMachineCyclotron) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineCyclotron) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineCyclotron) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineRadiolysis) {
			result.add(new FluidInfo(((TileEntityMachineRadiolysis) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineRadiolysis) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineRadiolysis) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityMachineTurbineGas) {
			result.add(new FluidInfo(((TileEntityMachineTurbineGas) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineTurbineGas) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineTurbineGas) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityMachineTurbineGas) te).tanks[3]));
			return result;
		}
		if (te instanceof TileEntityMachineSteamEngine) {
			result.add(new FluidInfo(((TileEntityMachineSteamEngine) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineSteamEngine) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineCombustionEngine) {
			result.add(new FluidInfo(((TileEntityMachineCombustionEngine) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineWoodBurner) {
			result.add(new FluidInfo(((TileEntityMachineWoodBurner) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineArcWelder) {
			result.add(new FluidInfo(((TileEntityMachineArcWelder) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineSolderingStation) {
			result.add(new FluidInfo(((TileEntityMachineSolderingStation) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineExcavator) {
			result.add(new FluidInfo(((TileEntityMachineExcavator) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineMiningLaser) {
			result.add(new FluidInfo(((TileEntityMachineMiningLaser) te).tankNew));
			return result;
		}
		if (te instanceof TileEntityMachineAutosaw) {
			result.add(new FluidInfo(((TileEntityMachineAutosaw) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineThresher) {
			result.add(new FluidInfo(((TileEntityMachineThresher) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineAnnihilator) {
			result.add(new FluidInfo(((TileEntityMachineAnnihilator) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineDrain) {
			result.add(new FluidInfo(((TileEntityMachineDrain) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineIntake) {
			result.add(new FluidInfo(((TileEntityMachineIntake) te).compair));
			return result;
		}
		if (te instanceof TileEntityMachinePumpSteam) {
			result.add(new FluidInfo(((TileEntityMachinePumpSteam) te).water));
			result.add(new FluidInfo(((TileEntityMachinePumpSteam) te).steam));
			result.add(new FluidInfo(((TileEntityMachinePumpSteam) te).lps));
			return result;
		}
		if (te instanceof TileEntityMachinePumpBase) {
			result.add(new FluidInfo(((TileEntityMachinePumpBase) te).water));
			return result;
		}
		if (te instanceof TileEntityRefueler) {
			result.add(new FluidInfo(((TileEntityRefueler) te).tank));
			return result;
		}
		if (te instanceof TileEntitySILEX) {
			result.add(new FluidInfo(((TileEntitySILEX) te).tank));
			return result;
		}
		if (te instanceof TileEntityStorageDrum) {
			result.add(new FluidInfo(((TileEntityStorageDrum) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityStorageDrum) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityLaunchpadSoyuz) {
			result.add(new FluidInfo(((TileEntityLaunchpadSoyuz) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityLaunchpadSoyuz) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntitySoyuzLauncher) {
			result.add(new FluidInfo(((TileEntitySoyuzLauncher) te).tanks[0]));
			result.add(new FluidInfo(((TileEntitySoyuzLauncher) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityPWRController) {
			result.add(new FluidInfo(((TileEntityPWRController) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityPWRController) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityReactorZirnox) {
			result.add(new FluidInfo(((TileEntityReactorZirnox) te).water));
			result.add(new FluidInfo(((TileEntityReactorZirnox) te).steam));
			result.add(new FluidInfo(((TileEntityReactorZirnox) te).carbonDioxide));
			return result;
		}
		if (te instanceof TileEntityWatz) {
			result.add(new FluidInfo(((TileEntityWatz) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityWatz) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityWatz) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityICF) {
			result.add(new FluidInfo(((TileEntityICF) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityICF) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityICF) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityICFPress) {
			result.add(new FluidInfo(((TileEntityICFPress) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityICFPress) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityCore) {
			result.add(new FluidInfo(((TileEntityCore) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityCore) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityCoreInjector) {
			result.add(new FluidInfo(((TileEntityCoreInjector) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityCoreInjector) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityCoreEmitter) {
			result.add(new FluidInfo(((TileEntityCoreEmitter) te).tank));
			return result;
		}
		if (te instanceof TileEntityCoreReceiver) {
			result.add(new FluidInfo(((TileEntityCoreReceiver) te).tank));
			return result;
		}
		if (te instanceof TileEntityFusionBoiler) {
			result.add(new FluidInfo(((TileEntityFusionBoiler) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityFusionBoiler) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityFusionBreeder) {
			result.add(new FluidInfo(((TileEntityFusionBreeder) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityFusionBreeder) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityFusionMHDT) {
			result.add(new FluidInfo(((TileEntityFusionMHDT) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityFusionMHDT) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityFusionKlystron) {
			result.add(new FluidInfo(((TileEntityFusionKlystron) te).compair));
			return result;
		}
		if (te instanceof TileEntityFusionPlasmaForge) {
			result.add(new FluidInfo(((TileEntityFusionPlasmaForge) te).inputTank));
			return result;
		}
		if (te instanceof TileEntityFusionTorus) {
			result.add(new FluidInfo(((TileEntityFusionTorus) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityFusionTorus) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityFusionTorus) te).tanks[2]));
			result.add(new FluidInfo(((TileEntityFusionTorus) te).tanks[3]));
			result.add(new FluidInfo(((TileEntityFusionTorus) te).coolantTanks[0]));
			result.add(new FluidInfo(((TileEntityFusionTorus) te).coolantTanks[1]));
			return result;
		}
		if (te instanceof TileEntityCooledBase) {
			result.add(new FluidInfo(((TileEntityCooledBase) te).coolantTanks[0]));
			result.add(new FluidInfo(((TileEntityCooledBase) te).coolantTanks[1]));
			return result;
		}
		if (te instanceof TileEntityPileVent) {
			result.add(new FluidInfo(((TileEntityPileVent) te).compair));
			return result;
		}
		if (te instanceof TileEntityRBMKBoiler) {
			result.add(new FluidInfo(((TileEntityRBMKBoiler) te).feed));
			result.add(new FluidInfo(((TileEntityRBMKBoiler) te).steam));
			return result;
		}
		if (te instanceof TileEntityRBMKHeater) {
			result.add(new FluidInfo(((TileEntityRBMKHeater) te).feed));
			result.add(new FluidInfo(((TileEntityRBMKHeater) te).steam));
			return result;
		}
		if (te instanceof TileEntityRBMKOutgasser) {
			result.add(new FluidInfo(((TileEntityRBMKOutgasser) te).gas));
			return result;
		}
		if (te instanceof TileEntityRBMKInlet) {
			result.add(new FluidInfo(((TileEntityRBMKInlet) te).water));
			return result;
		}
		if (te instanceof TileEntityRBMKOutlet) {
			result.add(new FluidInfo(((TileEntityRBMKOutlet) te).steam));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = getCore(world.getTileEntity(pos));
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).getMaxPower());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof TileEntityBatteryBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityBatteryBase) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityBatteryBase) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineDiesel diesel = ((TileEntityMachineDiesel) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFluidAmount() > 0) {
				tag.setBoolean(DataHelper.ACTIVE, true);
				tag.setDouble(DataHelper.OUTPUT, diesel.getHEFromFuel());
			} else {
				tag.setBoolean(DataHelper.ACTIVE, false);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, diesel.getPower());
			tag.setLong(DataHelper.CAPACITY, diesel.maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, diesel.tank);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			NBTTagCompound tag = new NBTTagCompound();

			if (((TileEntityMachineRTG) te).heat > 0) {
				tag.setBoolean(DataHelper.ACTIVE, true);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineRTG) te).heat);
			} else {
				tag.setBoolean(DataHelper.ACTIVE, false);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, true);
			tag.setDouble(DataHelper.OUTPUT, te.getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 70);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineTurbine) te).tanksNew[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineTurbine) te).tanksNew[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineShredder) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineShredder) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCentrifuge) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCentrifuge) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasCent) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasCent) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineGasCent) te).tank);
			return tag;
		}
		if (te instanceof TileEntityBarrel) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityBarrel) te).tankNew);
			return tag;
		}
		if (te instanceof TileEntityMachineAssemblyMachine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssemblyMachine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssemblyMachine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineAssemblyMachine) te).inputTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineAssemblyMachine) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityMachineAssemblyFactory) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssemblyFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssemblyFactory) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineAssemblyFactory) te).inputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineAssemblyFactory) te).inputTanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineAssemblyFactory) te).outputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineAssemblyFactory) te).water);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineAssemblyFactory) te).lps);
			return tag;
		}
		if (te instanceof TileEntityMachineLargeTurbine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineLargeTurbine) te).tanksNew[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineLargeTurbine) te).tanksNew[1]);
			return tag;
		}
		if (te instanceof TileEntitySolarBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("heat", ((TileEntitySolarBoiler) te).heat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntitySolarBoiler) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntitySolarBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineFractionTower) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineFractionTower) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineFractionTower) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineFractionTower) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineCrystallizer) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineCrystallizer) te).getPowerRequired());
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCrystallizer) te).tankNew);
			return tag;
		}
		if (te instanceof TileEntityCondenser) {
			NBTTagCompound tag = new NBTTagCompound();
			if (te instanceof TileEntityCondenserPowered) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityCondenserPowered) te).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityCondenserPowered) te).getMaxPower());
			}
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCondenser) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityCondenser) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityChungus) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityChungus) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityChungus) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityChungus) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityChungus) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineIndustrialTurbine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineIndustrialTurbine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineIndustrialTurbine) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineIndustrialTurbine) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineIndustrialTurbine) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineChemicalPlant) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemicalPlant) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemicalPlant) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineChemicalPlant) te).inputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineChemicalPlant) te).inputTanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineChemicalPlant) te).inputTanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineChemicalPlant) te).outputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineChemicalPlant) te).outputTanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineChemicalFactory) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemicalFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemicalFactory) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineChemicalFactory) te).inputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineChemicalFactory) te).inputTanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineChemicalFactory) te).outputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineChemicalFactory) te).water);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineChemicalFactory) te).lps);
			return tag;
		}
		if (te instanceof TileEntityMachinePUREX) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePUREX) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePUREX) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePUREX) te).inputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePUREX) te).inputTanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachinePUREX) te).inputTanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachinePUREX) te).outputTanks[0]);
			return tag;
		}
		if (te instanceof TileEntityMachineRockMill) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRockMill) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRockMill) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineRockMill) te).inputTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineRockMill) te).outputTanks[0]);
			return tag;
		}
		if (te instanceof TileEntityMachinePrecAss) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePrecAss) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePrecAss) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePrecAss) te).inputTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePrecAss) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityMachineSuperComputer) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSuperComputer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSuperComputer) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineSuperComputer) te).inputTank);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineSuperComputer) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbofan) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbofan) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineTurbofan) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineRadGen) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadGen) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadGen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasFlare) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasFlare) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineGasFlare) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineOilWell) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineOilWell) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineOilWell) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineOilWell) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineOilWell) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachinePumpjack) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePumpjack) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePumpjack) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePumpjack) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineFrackingTower) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineFrackingTower) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineFrackingTower) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineFrackingTower) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineFrackingTower) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineFrackingTower) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineRefinery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRefinery) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRefinery) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineRefinery) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineRefinery) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineRefinery) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineRefinery) te).tanks[3]);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineRefinery) te).tanks[4]);
			return tag;
		}
		if (te instanceof TileEntityMachineCatalyticCracker) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCatalyticCracker) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineCatalyticCracker) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineCatalyticCracker) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineCatalyticCracker) te).tanks[3]);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineCatalyticCracker) te).tanks[4]);
			return tag;
		}
		if (te instanceof TileEntityMachineCatalyticReformer) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCatalyticReformer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCatalyticReformer) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCatalyticReformer) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineCatalyticReformer) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineCatalyticReformer) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineCatalyticReformer) te).tanks[3]);
			return tag;
		}
		if (te instanceof TileEntityMachineHydrotreater) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineHydrotreater) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineHydrotreater) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineHydrotreater) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineHydrotreater) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineHydrotreater) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineHydrotreater) te).tanks[3]);
			return tag;
		}
		if (te instanceof TileEntityMachineVacuumDistill) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineVacuumDistill) te).isOn);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineVacuumDistill) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineVacuumDistill) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineVacuumDistill) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineVacuumDistill) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineVacuumDistill) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineVacuumDistill) te).tanks[3]);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineVacuumDistill) te).tanks[4]);
			return tag;
		}
		if (te instanceof TileEntityMachineCoker) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityMachineCoker) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityMachineCoker.maxHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCoker) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineCoker) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachinePyroOven) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachinePyroOven) te).isProgressing);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePyroOven) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePyroOven) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePyroOven) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePyroOven) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineLiquefactor) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineLiquefactor) te).usage);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLiquefactor) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLiquefactor) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineLiquefactor) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSolidifier) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineSolidifier) te).usage);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSolidifier) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSolidifier) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineSolidifier) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineFluidTank) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineFluidTank) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineUF6Tank) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineUF6Tank) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachinePuF6Tank) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePuF6Tank) te).tank);
			return tag;
		}
		if (te instanceof TileEntityHeatBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityHeatBoiler) te).isOn);
			tag.setLong(DataHelper.HEAT, ((TileEntityHeatBoiler) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityHeatBoiler.maxHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityHeatBoiler) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityHeatBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityHeatBoilerIndustrial) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityHeatBoilerIndustrial) te).isOn);
			tag.setLong(DataHelper.HEAT, ((TileEntityHeatBoilerIndustrial) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityHeatBoilerIndustrial.maxHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityHeatBoilerIndustrial) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityHeatBoilerIndustrial) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityHeaterElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityHeaterElectric) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityHeaterElectric) te).getConsumption());
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityHeaterElectric) te).getHeatGen());
			tag.setLong(DataHelper.ENERGY, ((TileEntityHeaterElectric) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityHeaterElectric) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityHeaterOilburner) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityHeaterOilburner) te).isOn);
			tag.setLong(DataHelper.HEAT, ((TileEntityHeaterOilburner) te).heatEnergy);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityHeaterOilburner) te).tank);
			return tag;
		}
		if (te instanceof TileEntityHeaterHeatex) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityHeaterHeatex) te).heatEnergy);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityHeaterHeatex) te).tanksNew[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityHeaterHeatex) te).tanksNew[1]);
			return tag;
		}
		if (te instanceof TileEntityFurnaceCombination) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityFurnaceCombination) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityFurnaceCombination.maxHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFurnaceCombination) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineBlastFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineBlastFurnace) te).isProgressing);
			tag.setInteger(DataHelper.FUEL, ((TileEntityMachineBlastFurnace) te).fuel);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineBlastFurnace) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineBlastFurnace) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineRotaryFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineRotaryFurnace) te).isProgressing);
			tag.setLong(DataHelper.HEAT, (long) ((TileEntityMachineRotaryFurnace) te).burnHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineRotaryFurnace) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineRotaryFurnace) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineRotaryFurnace) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineStrandCaster) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineStrandCaster) te).water);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineStrandCaster) te).steam);
			return tag;
		}
		if (te instanceof TileEntityMachineHephaestus) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityMachineHephaestus) te).bufferedHeat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineHephaestus) te).input);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineHephaestus) te).output);
			return tag;
		}
		if (te instanceof TileEntityElectrolyser) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityElectrolyser) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityElectrolyser) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityElectrolyser) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityElectrolyser) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityElectrolyser) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityElectrolyser) te).tanks[3]);
			return tag;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityDeuteriumExtractor) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityDeuteriumExtractor) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityDeuteriumExtractor) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityDeuteriumExtractor) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineMixer) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineMixer) te).getConsumption());
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMixer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMixer) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineMixer) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineMixer) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineMixer) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineOreSlopper) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineOreSlopper) te).processing);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineOreSlopper) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineOreSlopper) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineOreSlopper) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineOreSlopper) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineOreSlopper) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineCompressorBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCompressorBase) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineCompressorBase) te).powerRequirement);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCompressorBase) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCompressorBase) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCompressorBase) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineCompressorBase) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineCyclotron) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineCyclotron) te).getConsumption());
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCyclotron) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCyclotron) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCyclotron) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineCyclotron) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineCyclotron) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineExposureChamber) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineExposureChamber) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineExposureChamber) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineExposureChamber) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineExposureChamber) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRadiolysis) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityMachineRadiolysis) te).heat);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadiolysis) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadiolysis) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineRadiolysis) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineRadiolysis) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineRadiolysis) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityMachineElectricFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineElectricFurnace) te).isProcessing());
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineElectricFurnace) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineElectricFurnace) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineElectricFurnace) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineEPress) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityConveyorPress) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityConveyorPress) te).usage);
			tag.setLong(DataHelper.ENERGY, ((TileEntityConveyorPress) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityConveyorPress) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineAutocrafter) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, TileEntityMachineAutocrafter.consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAutocrafter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAutocrafter) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnaceLarge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineArcFurnaceLarge) te).isProgressing);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineArcFurnaceLarge) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineArcFurnaceLarge) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineArcWelder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineArcWelder) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineArcWelder) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineArcWelder) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineArcWelder) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSolderingStation) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineSolderingStation) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSolderingStation) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSolderingStation) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineSolderingStation) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineExcavator) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineExcavator) te).consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineExcavator) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineExcavator) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineExcavator) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineMiningLaser) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineMiningLaser) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, TileEntityMachineMiningLaser.consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiningLaser) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiningLaser) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineMiningLaser) te).tankNew);
			return tag;
		}
		if (te instanceof TileEntityMachineWoodBurner) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineWoodBurner) te).isOn);
			tag.setInteger(DataHelper.FUEL, ((TileEntityMachineWoodBurner) te).burnTime);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineWoodBurner) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineWoodBurner) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineWoodBurner) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineCombustionEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCombustionEngine) te).isOn);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCombustionEngine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCombustionEngine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCombustionEngine) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbineGas) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineTurbineGas) te).instantPowerOutput);
			tag.setInteger("rpm", ((TileEntityMachineTurbineGas) te).rpm);
			tag.setLong(DataHelper.HEAT, ((TileEntityMachineTurbineGas) te).temp);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbineGas) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbineGas) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineTurbineGas) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineTurbineGas) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineTurbineGas) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineTurbineGas) te).tanks[3]);
			return tag;
		}
		if (te instanceof TileEntityMachineSteamEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSteamEngine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSteamEngine) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineSteamEngine) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineSteamEngine) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityStirling) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityStirling) te).heat);
			tag.setLong(DataHelper.MAXHEAT, ((TileEntityStirling) te).maxHeat());
			tag.setLong(DataHelper.ENERGY, ((TileEntityStirling) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityStirling) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineIntake) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineIntake) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineIntake) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineIntake) te).compair);
			return tag;
		}
		if (te instanceof TileEntityMachinePumpSteam) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachinePumpSteam) te).isOn);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePumpSteam) te).water);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePumpSteam) te).steam);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachinePumpSteam) te).lps);
			return tag;
		}
		if (te instanceof TileEntityMachinePumpElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachinePumpElectric) te).isOn);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePumpElectric) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePumpElectric) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePumpElectric) te).water);
			return tag;
		}
		if (te instanceof TileEntityMachineAutosaw) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineAutosaw) te).isOn);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineAutosaw) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineThresher) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineThresher) te).isOn);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineThresher) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineAnnihilator) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineAnnihilator) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineDrain) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineDrain) te).tank);
			return tag;
		}
		if (te instanceof TileEntityRefueler) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRefueler) te).tank);
			return tag;
		}
		if (te instanceof TileEntitySILEX) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntitySILEX) te).tank);
			return tag;
		}
		if (te instanceof TileEntityStorageDrum) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityStorageDrum) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityStorageDrum) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCharger) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityCharger) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCharger) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMicrowave) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.CONSUMPTION, TileEntityMicrowave.consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMicrowave) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMicrowave) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTeleporter) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineTeleporter) te).linked);
			tag.setDouble(DataHelper.CONSUMPTION, TileEntityMachineTeleporter.consumption);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTeleporter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTeleporter) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRadarNT) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadarNT) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadarNT) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityForceField) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityForceField) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityForceField) te).powerCons);
			tag.setLong(DataHelper.ENERGY, ((TileEntityForceField) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityForceField) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityLaunchpadSoyuz) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityLaunchpadSoyuz) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityLaunchpadSoyuz) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityLaunchpadSoyuz) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityLaunchpadSoyuz) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntitySoyuzLauncher) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySoyuzLauncher) te).starting);
			tag.setLong(DataHelper.ENERGY, ((TileEntitySoyuzLauncher) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntitySoyuzLauncher) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntitySoyuzLauncher) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntitySoyuzLauncher) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityFEL) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityFEL) te).isOn);
			tag.setDouble(DataHelper.CONSUMPTION, TileEntityFEL.powerReq);
			tag.setLong(DataHelper.ENERGY, ((TileEntityFEL) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityFEL) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityICF) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityICF) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityICF.maxHeat);
			tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityICF) te).consumption);
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityICF) te).output);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityICF) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityICF) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityICF) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityICFController) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityICFController) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityICFController) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityICFPress) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityICFPress) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityICFPress) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCore) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.HEAT, ((TileEntityCore) te).heat);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCore) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityCore) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCoreInjector) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCoreInjector) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityCoreInjector) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCoreEmitter) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityCoreEmitter) te).isOn);
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityCoreEmitter) te).watts);
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreEmitter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreEmitter) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCoreEmitter) te).tank);
			return tag;
		}
		if (te instanceof TileEntityCoreReceiver) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityCoreReceiver) te).joules);
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreReceiver) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreReceiver) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCoreReceiver) te).tank);
			return tag;
		}
		if (te instanceof TileEntityCoreStabilizer) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityCoreStabilizer) te).isOn);
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityCoreStabilizer) te).watts);
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreStabilizer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreStabilizer) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityPWRController) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityPWRController) te).assembled);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityPWRController) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityPWRController) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityReactorZirnox) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityReactorZirnox) te).isOn);
			tag.setLong(DataHelper.HEAT, ((TileEntityReactorZirnox) te).heat);
			tag.setLong(DataHelper.MAXHEAT, TileEntityReactorZirnox.maxHeat);
			tag.setLong(DataHelper.PRESSURE, ((TileEntityReactorZirnox) te).pressure);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityReactorZirnox) te).water);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityReactorZirnox) te).steam);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityReactorZirnox) te).carbonDioxide);
			return tag;
		}
		if (te instanceof TileEntityWatz) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityWatz) te).isOn);
			tag.setLong(DataHelper.HEAT, ((TileEntityWatz) te).heat);
			tag.setDouble("flux", ((TileEntityWatz) te).fluxDisplay);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityWatz) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityWatz) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityWatz) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityFusionBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGYHU, ((TileEntityFusionBoiler) te).plasmaEnergy);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionBoiler) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityFusionBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityFusionBreeder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.ENERGYHU, ((TileEntityFusionBreeder) te).neutronEnergy);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionBreeder) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityFusionBreeder) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityFusionMHDT) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityFusionMHDT) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityFusionMHDT) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionMHDT) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityFusionMHDT) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityFusionKlystron) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityFusionKlystron) te).output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityFusionKlystron) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityFusionKlystron) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionKlystron) te).compair);
			return tag;
		}
		if (te instanceof TileEntityFusionPlasmaForge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGYHU, ((TileEntityFusionPlasmaForge) te).plasmaEnergy);
			tag.setLong(DataHelper.ENERGY, ((TileEntityFusionPlasmaForge) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityFusionPlasmaForge) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionPlasmaForge) te).inputTank);
			return tag;
		}
		if (te instanceof TileEntityFusionTorus) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGYHU, ((TileEntityFusionTorus) te).plasmaEnergy);
			tag.setLong(DataHelper.ENERGYKU, ((TileEntityFusionTorus) te).klystronEnergy);
			tag.setLong(DataHelper.ENERGY, ((TileEntityFusionTorus) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityFusionTorus) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFusionTorus) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityFusionTorus) te).tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityFusionTorus) te).tanks[2]);
			FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityFusionTorus) te).tanks[3]);
			FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityFusionTorus) te).coolantTanks[0]);
			return tag;
		}
		if (te instanceof TileEntityCooledBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityCooledBase) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCooledBase) te).getMaxPower());
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCooledBase) te).coolantTanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityCooledBase) te).coolantTanks[1]);
			return tag;
		}
		if (te instanceof TileEntityPileVent) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityPileVent) te).isActive);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityPileVent) te).compair);
			return tag;
		}
		if (te instanceof TileEntityRBMKInlet) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKInlet) te).water);
			return tag;
		}
		if (te instanceof TileEntityRBMKOutlet) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKOutlet) te).steam);
			return tag;
		}
		if (te instanceof TileEntityRBMKBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("heatD", ((TileEntityRBMKBase) te).heat);
			if (te instanceof TileEntityRBMKBoiler) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKBoiler) te).feed);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityRBMKBoiler) te).steam);
			}
			if (te instanceof TileEntityRBMKHeater) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKHeater) te).feed);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityRBMKHeater) te).steam);
			}
			if (te instanceof TileEntityRBMKOutgasser)
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKOutgasser) te).gas);
			if (te instanceof TileEntityRBMKControl) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityRBMKControl) te).hasPower);
				tag.setLong(DataHelper.ENERGY, ((TileEntityRBMKControl) te).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityRBMKControl) te).getMaxPower());
			}
			if (te instanceof TileEntityRBMKRod) {
				TileEntityRBMKRod rod = (TileEntityRBMKRod) te;
				ItemStack stack = rod.inventory.getStackInSlot(0);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemRBMKRod) {
					tag.setDouble("fluxFast", rod.lastFluxQuantity * rod.lastFluxRatio);
					tag.setDouble("fluxSlow", rod.lastFluxQuantity * (1.0D - rod.lastFluxRatio));
					tag.setDouble("depletion", ((1.0D - ItemRBMKRod.getEnrichment(stack)) * 100000.0D) / 1000.0D);
					tag.setDouble("xenon", ItemRBMKRod.getPoison(stack));
					tag.setDouble("skin", ItemRBMKRod.getHullHeat(stack));
					tag.setDouble("c_heat", ItemRBMKRod.getCoreHeat(stack));
					tag.setDouble("melt", ((ItemRBMKRod) stack.getItem()).meltingPoint);
				}
			}
			return tag;
		}
		if (te instanceof TileEntityGeiger) {
			NBTTagCompound tag = new NBTTagCompound();
			double rads = (int) (((TileEntityGeiger) te).check() * 10.0F) / 10.0D;
			String chunkPrefix = ContaminationUtil.getPreffixFromRad(rads);
			tag.setString("chunkRad", chunkPrefix + rads + " RAD/s");
			return tag;
		}
		return null;
	}

	public ArrayList getHookValues(TileEntity te) {
		/*ArrayList values = HBMHooks.map.get(te);
		if (values == null)
			HBMHooks.map.put(te, null);
		return values;*/
		return null;
	}

	@Override
	public void registerItems(Register<Item> event) {
		ItemKitMain.register(ItemKitHBM::new);
		ItemCardMain.register(ItemCardHBM::new);
	}

	@Override
	public void loadOreInfo() {
		loadOre(1, 32, 30, 10, ModBlocks.gas_flammable);
		loadOre(1, 32, 30, 10, ModBlocks.gas_explosive);
		loadOre(getSpawn(CompatibilityConfig.gneissIronSpawn), 6, 30, 10, ModBlocks.ore_gneiss_iron);
		loadOre(getSpawn(CompatibilityConfig.gneissGoldSpawn), 6, 30, 10, ModBlocks.ore_gneiss_gold);
		loadOre(getSpawn(CompatibilityConfig.uraniumSpawn) * 3, 6, 30, 10, ModBlocks.ore_gneiss_uranium);
		loadOre(getSpawn(CompatibilityConfig.copperSpawn) * 3, 6, 30, 10, ModBlocks.ore_gneiss_copper);
		loadOre(getSpawn(CompatibilityConfig.asbestosSpawn) * 3, 6, 30, 10, ModBlocks.ore_gneiss_asbestos);
		loadOre(getSpawn(CompatibilityConfig.lithiumSpawn), 6, 30, 10, ModBlocks.ore_gneiss_lithium);
		loadOre(getSpawn(CompatibilityConfig.rareSpawn), 6, 30, 10, ModBlocks.ore_gneiss_asbestos);
		loadOre(getSpawn(CompatibilityConfig.gassshaleSpawn) * 3, 10, 30, 10, ModBlocks.ore_gneiss_gas);
		loadOre(getSpawn(CompatibilityConfig.uraniumSpawn), 5, 5, 20, ModBlocks.ore_uranium);
		loadOre(getSpawn(CompatibilityConfig.thoriumSpawn), 5, 5, 25, ModBlocks.ore_thorium);
		loadOre(getSpawn(CompatibilityConfig.titaniumSpawn), 6, 5, 30, ModBlocks.ore_titanium);
		loadOre(getSpawn(CompatibilityConfig.sulfurSpawn), 8, 5, 30, ModBlocks.ore_sulfur);
		loadOre(getSpawn(CompatibilityConfig.aluminiumSpawn), 6, 5, 40, ModBlocks.ore_aluminium);
		loadOre(getSpawn(CompatibilityConfig.copperSpawn), 6, 5, 45, ModBlocks.ore_copper);
		loadOre(getSpawn(CompatibilityConfig.fluoriteSpawn), 4, 5, 45, ModBlocks.ore_fluorite);
		loadOre(getSpawn(CompatibilityConfig.niterSpawn), 6, 5, 30, ModBlocks.ore_niter);
		loadOre(getSpawn(CompatibilityConfig.tungstenSpawn), 8, 5, 30, ModBlocks.ore_tungsten);
		loadOre(getSpawn(CompatibilityConfig.leadSpawn), 9, 5, 30, ModBlocks.ore_lead);
		loadOre(getSpawn(CompatibilityConfig.berylliumSpawn), 4, 5, 30, ModBlocks.ore_beryllium);
		loadOre(getSpawn(CompatibilityConfig.rareSpawn), 5, 5, 20, ModBlocks.ore_rare);
		loadOre(getSpawn(CompatibilityConfig.ligniteSpawn), 24, 35, 25, ModBlocks.ore_lignite);
		loadOre(getSpawn(CompatibilityConfig.asbestosSpawn), 4, 16, 16, ModBlocks.ore_asbestos);
		loadOre(getSpawn(CompatibilityConfig.cinnabarSpawn), 4, 8, 16, ModBlocks.ore_cinnabar);
		loadOre(getSpawn(CompatibilityConfig.cobaltSpawn), 4, 4, 8, ModBlocks.ore_cobalt);
		loadOre(getSpawn(CompatibilityConfig.ironClusterSpawn), 6, 15, 45, ModBlocks.cluster_iron);
		loadOre(getSpawn(CompatibilityConfig.titaniumClusterSpawn), 6, 15, 30, ModBlocks.cluster_titanium);
		loadOre(getSpawn(CompatibilityConfig.aluminiumClusterSpawn), 6, 15, 35, ModBlocks.cluster_aluminium);
	}

	private int getSpawn(Map<Integer, Integer> map) {
		if (map == null)
			return 0;
		Integer value = map.get(0);
		return value == null ? 0 : value;
	}

	private void loadOre(int veinCount, int amount, int minHeight, int variance, Block block) {
		EnergyControl.oreHelper.put(OreHelper.getId(block, 0), new OreHelper(minHeight, minHeight + variance, amount, veinCount));
	}
}
