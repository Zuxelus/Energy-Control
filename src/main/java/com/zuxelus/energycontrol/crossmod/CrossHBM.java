package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IItemFluidHandler;
import com.hbm.inventory.FusionRecipes;
import com.hbm.inventory.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemCatalyst;
import com.hbm.items.special.ItemAMSCore;
import com.hbm.saveddata.RadiationSavedData;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.*;
import com.hbm.world.generator.DungeonToolbox;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CrossHBM extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("euType", "HE");
		if (te instanceof TileEntityMachineBattery) {
			tag.setDouble("storage", ((TileEntityMachineBattery) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			tag.setDouble("storage", ((TileEntityMachineCoal) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineCoal) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble("storage", ((TileEntityMachineDiesel) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineDiesel) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			tag.setDouble("storage", ((TileEntityMachineSeleniumEngine) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineSeleniumEngine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble("storage", ((TileEntityMachineRTG) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble("storage", ((TileEntityMachineMiniRTG) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			tag.setDouble("storage", ((TileEntityMachineAmgen) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			tag.setDouble("storage", ((TileEntityMachineSPP) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			tag.setDouble("storage", ((TileEntityMachineTurbine) te).getSPower());
			tag.setDouble("maxStorage", ((TileEntityMachineTurbine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			tag.setDouble("storage", ((TileEntityMachineShredder) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineShredder) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			tag.setDouble("storage", ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineBoilerElectric) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			tag.setDouble("storage", ((TileEntityMachineArcFurnace) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineArcFurnace) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			tag.setDouble("storage", ((TileEntityMachineCentrifuge) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineCentrifuge) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			tag.setDouble("storage", ((TileEntityMachineGasCent) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineGasCent) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				tag.setDouble("storage", ((TileEntityMachineLargeTurbine) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityMachineLargeTurbine) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				tag.setDouble("storage", ((TileEntityMachineIGenerator) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityMachineIGenerator) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				tag.setDouble("storage", ((TileEntityITER) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityITER) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				tag.setDouble("storage", ((TileEntityMachinePlasmaHeater) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachinePlasmaHeater) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineCrystallizer) {
				tag.setDouble("storage", ((TileEntityMachineCrystallizer) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineCrystallizer) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorld().getTileEntity(((TileEntityDummy) te).target);
			if (base instanceof TileEntityMachineGasFlare) {
				tag.setDouble("storage", ((TileEntityMachineGasFlare) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityMachineGasFlare) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				tag.setDouble("storage", ((TileEntityMachineTurbofan) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityMachineTurbofan) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setDouble("storage", ((TileEntityMachineRadGen) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityMachineRadGen) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				tag.setDouble("storage", ((TileEntityAMSLimiter) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityAMSLimiter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				tag.setDouble("storage", ((TileEntityAMSBase) base).getSPower());
				tag.setDouble("maxStorage", ((TileEntityAMSBase) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				tag.setDouble("storage", ((TileEntityAMSEmitter) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityAMSEmitter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setDouble("storage", ((TileEntityMachineOilWell) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineOilWell) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				tag.setDouble("storage", ((TileEntityMachineAssembler) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setDouble("storage", ((TileEntityMachinePumpjack) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachinePumpjack) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				tag.setDouble("storage", ((TileEntityMachineRefinery) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineRefinery) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					tag.setDouble("storage", ((TileEntityWatzCore) core).getSPower());
					tag.setDouble("maxStorage", ((TileEntityWatzCore) core).maxPower);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					tag.setDouble("storage", ((TileEntityFusionMultiblock) core).getSPower());
					tag.setDouble("maxStorage", ((TileEntityFusionMultiblock) core).maxPower);
					return tag;
				}
			}
		}
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityMachineCoal) {
			result.add(new FluidInfo(((TileEntityMachineCoal) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineDiesel) {
			result.add(new FluidInfo(((TileEntityMachineDiesel) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			result.add(new FluidInfo(((TileEntityMachineSeleniumEngine) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineTurbine) {
			result.add(new FluidInfo(((TileEntityMachineTurbine) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineTurbine) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineBoiler) {
			result.add(new FluidInfo(((TileEntityMachineBoiler) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineBoiler) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			result.add(new FluidInfo(((TileEntityMachineBoilerElectric) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineBoilerElectric) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineGasCent) {
			result.add(new FluidInfo(((TileEntityMachineGasCent) te).tank));
			return result;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				result.add(new FluidInfo(((TileEntityMachineLargeTurbine) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineLargeTurbine) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntitySolarBoiler) {
				IFluidTankProperties tanks[] = ((TileEntitySolarBoiler) base).getTankProperties();
				result.add(new FluidInfo(tanks[0].getContents(), tanks[0].getCapacity()));
				result.add(new FluidInfo(tanks[1].getContents(), tanks[1].getCapacity()));
				return result;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				result.add(new FluidInfo(((TileEntityMachineIGenerator) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineIGenerator) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityMachineIGenerator) base).tanks[2]));
				return result;
			}
			if (base instanceof TileEntityITER) {
				result.add(new FluidInfo(((TileEntityITER) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityITER) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityITER) base).plasma));
				return result;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				result.add(new FluidInfo(((TileEntityMachinePlasmaHeater) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachinePlasmaHeater) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityMachinePlasmaHeater) base).plasma));
				return result;
			}
			if (base instanceof TileEntityMachineOrbus) {
				result.add(new FluidInfo(((TileEntityMachineOrbus) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineFractionTower) {
				result.add(new FluidInfo(((TileEntityMachineFractionTower) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineFractionTower) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityMachineFractionTower) base).tanks[2]));
				return result;
			}
			if (base instanceof TileEntityMachineCrystallizer) {
				result.add(new FluidInfo(((TileEntityMachineCrystallizer) base).tank));
				return result;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorld().getTileEntity(((TileEntityDummy) te).target);
			if (base instanceof TileEntityMachineFluidTank) {
				result.add(new FluidInfo(((TileEntityMachineFluidTank) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineGasFlare) {
				result.add(new FluidInfo(((TileEntityMachineGasFlare) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				result.add(new FluidInfo(((TileEntityMachineTurbofan) base).tank));
				return result;
			}
			if (base instanceof TileEntityAMSLimiter) {
				result.add(new FluidInfo(((TileEntityAMSLimiter) base).tank));
				return result;
			}
			if (base instanceof TileEntityAMSBase) {
				result.add(new FluidInfo(((TileEntityAMSBase) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityAMSBase) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityAMSBase) base).tanks[2]));
				result.add(new FluidInfo(((TileEntityAMSBase) base).tanks[3]));
				return result;
			}
			if (base instanceof TileEntityAMSEmitter) {
				result.add(new FluidInfo(((TileEntityAMSEmitter) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineOilWell) {
				result.add(new FluidInfo(((TileEntityMachineOilWell) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineOilWell) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				result.add(new FluidInfo(((TileEntityMachinePumpjack) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachinePumpjack) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntityMachineRefinery) {
				result.add(new FluidInfo(((TileEntityMachineRefinery) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineRefinery) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityMachineRefinery) base).tanks[2]));
				result.add(new FluidInfo(((TileEntityMachineRefinery) base).tanks[3]));
				result.add(new FluidInfo(((TileEntityMachineRefinery) base).tanks[4]));
				return result;
			}
		}
		if (te instanceof TileEntityMachineReactorSmall) {
			result.add(new FluidInfo(((TileEntityMachineReactorSmall) te).tanks[0]));
			result.add(new FluidInfo(((TileEntityMachineReactorSmall) te).tanks[1]));
			result.add(new FluidInfo(((TileEntityMachineReactorSmall) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityReactorHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 2));
				if (core instanceof TileEntityMachineReactorLarge) {
					result.add(new FluidInfo(((TileEntityMachineReactorLarge) core).tanks[0]));
					result.add(new FluidInfo(((TileEntityMachineReactorLarge) core).tanks[1]));
					result.add(new FluidInfo(((TileEntityMachineReactorLarge) core).tanks[2]));
					return result;
				}
			}
		}
		if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					result.add(new FluidInfo(((TileEntityWatzCore) core).tank));
					return result;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					result.add(new FluidInfo(((TileEntityFusionMultiblock) core).tanks[0]));
					result.add(new FluidInfo(((TileEntityFusionMultiblock) core).tanks[1]));
					result.add(new FluidInfo(((TileEntityFusionMultiblock) core).tanks[2]));
					return result;
				}
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineBattery) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", ((TileEntityMachineCoal) te).burnTime > 0);
			tag.setLong("stored", ((TileEntityMachineCoal) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineCoal) te).maxPower);
			if (((TileEntityMachineCoal) te).burnTime > 0)
				tag.setDouble("output", 25);
			else
				tag.setDouble("output", 0);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineCoal) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineDiesel diesel = ((TileEntityMachineDiesel) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFluidAmount() > 0) {
				tag.setBoolean("active", true);
				tag.setDouble("output", 10);
				tag.setDouble("output", diesel.getHEFromFuel());
			} else {
				tag.setBoolean("active", false);
				tag.setDouble("output", 0);
				tag.setDouble("output", 0);
			}
			tag.setLong("stored", ((TileEntityMachineDiesel) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineDiesel) te).maxPower);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineDiesel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineSeleniumEngine diesel = ((TileEntityMachineSeleniumEngine) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFluidAmount() > 0 && diesel.pistonCount > 2) {
				tag.setBoolean("active", true);
				tag.setDouble("output", diesel.getHEFromFuel() * Math.pow(diesel.pistonCount, 1.15D));
			} else {
				tag.setBoolean("active", false);
				tag.setDouble("output", 0);
			}
			tag.setLong("stored", ((TileEntityMachineSeleniumEngine) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineSeleniumEngine) te).maxPower);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineSeleniumEngine) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			NBTTagCompound tag = new NBTTagCompound();

			if (((TileEntityMachineRTG) te).heat > 0) {
				tag.setBoolean("active", true);
				tag.setDouble("output", ((TileEntityMachineRTG) te).heat);
			} else {
				tag.setBoolean("active", false);
				tag.setDouble("output", 0);
			}
			tag.setLong("stored", ((TileEntityMachineRTG) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", true);
			tag.setDouble("output", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 70);
			tag.setLong("stored", ((TileEntityMachineMiniRTG) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = calcAmgenOutput(te.getWorld(), te.getBlockType(), te.getPos());
			tag.setBoolean("active", output > 0);
			tag.setDouble("output", output);
			tag.setLong("stored", ((TileEntityMachineAmgen) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = Math.max(((TileEntityMachineSPP) te).checkStructure() * 15, 0);
			tag.setBoolean("active", output > 0);
			tag.setDouble("output", output);
			tag.setLong("stored", ((TileEntityMachineSPP) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine turbine = (TileEntityMachineTurbine) te;
			NBTTagCompound tag = new NBTTagCompound();
			double output = 0;
			double consumption = 0;
			Object[] outs = MachineRecipes.getTurbineOutput(getFluid(turbine.tanks[0],turbine.inventory.getStackInSlot(2)));
			if (outs != null) {
				consumption = ((Integer)outs[2]).intValue() * 1200;
				output = ((Integer) outs[3]).intValue() * 1200;
			}
			tag.setBoolean("active", output > 0);
			tag.setDouble("consumption", consumption);
			tag.setDouble("output", output);
			tag.setLong("stored", ((TileEntityMachineTurbine) te).getSPower());
			tag.setLong("capacity", ((TileEntityMachineTurbine) te).maxPower);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineTurbine) te).tanks[0]);
			FluidInfo.addTank("tank2", tag, ((TileEntityMachineTurbine) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineShredder) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineShredder) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("hull", ((TileEntityMachineBoiler) te).heat / 100);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineBoiler) te).tanks[0]);
			FluidInfo.addTank("tank2", tag, ((TileEntityMachineBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineBoilerElectric) te).maxPower);
			tag.setLong("hull", ((TileEntityMachineBoilerElectric) te).heat / 100);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineBoilerElectric) te).tanks[0]);
			FluidInfo.addTank("tank2", tag, ((TileEntityMachineBoilerElectric) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineArcFurnace) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineArcFurnace) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineCentrifuge) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineCentrifuge) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineGasCent) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineGasCent) te).maxPower);
			FluidInfo.addTank("tank", tag, ((TileEntityMachineGasCent) te).tank);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				double output = 0; // TODO
				tag.setBoolean("active", output > 0);
				tag.setDouble("output", output);
				tag.setLong("stored", ((TileEntityMachineLargeTurbine) base).getSPower());
				tag.setLong("capacity", ((TileEntityMachineLargeTurbine) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineLargeTurbine) base).tanks[0]);
				FluidInfo.addTank("tank2", tag, ((TileEntityMachineLargeTurbine) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntitySolarBoiler) {
				IFluidTankProperties tanks[] = ((TileEntitySolarBoiler) base).getTankProperties();
				FluidInfo.addTank("tank", tag, tanks[0].getContents());
				FluidInfo.addTank("tank2", tag, tanks[1].getContents());
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				TileEntityMachineIGenerator generator = (TileEntityMachineIGenerator) base;
				double output = calcIGeneratorOutput(generator);
				tag.setBoolean("active", output > 0);
				tag.setDouble("output", output);
				tag.setLong("stored", generator.getSPower());
				tag.setLong("capacity", generator.maxPower);
				tag.setInteger("temp", generator.temperature + 300);
				tag.setInteger("speed", generator.torque);
				FluidInfo.addTank("tank", tag, generator.tanks[0]);
				FluidInfo.addTank("tank2", tag, generator.tanks[1]);
				FluidInfo.addTank("tank3", tag, generator.tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				TileEntityITER reactor = (TileEntityITER) base;
				tag.setLong("stored", reactor.getPower());
				tag.setLong("capacity", reactor.maxPower);
				int output = FusionRecipes.getSteamProduction(reactor.plasmaType) * 20;
				tag.setDouble("consumption", output * 10);
				tag.setDouble("outputmb", output);
				FluidInfo.addTank("tank", tag, reactor.tanks[0]);
				FluidInfo.addTank("tank2", tag, reactor.tanks[1]);
				FluidInfo.addTank("tank3", tag, reactor.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				TileEntityMachinePlasmaHeater heater = (TileEntityMachinePlasmaHeater) base;
				tag.setLong("stored", heater.getPower());
				tag.setLong("capacity", heater.maxPower);
				FluidInfo.addTank("tank", tag, heater.tanks[0]);
				FluidInfo.addTank("tank2", tag, heater.tanks[1]);
				FluidInfo.addTank("tank3", tag, heater.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachineOrbus) {
				FluidInfo.addTank("tank", tag, ((TileEntityMachineOrbus) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineFractionTower) {
				FluidInfo.addTank("tank", tag, ((TileEntityMachineFractionTower) base).tanks[0]);
				FluidInfo.addTank("tank2", tag, ((TileEntityMachineFractionTower) base).tanks[1]);
				FluidInfo.addTank("tank3", tag, ((TileEntityMachineFractionTower) base).tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityMachineCrystallizer) {
				tag.setLong("stored", ((TileEntityMachineCrystallizer) base).getPower());
				tag.setLong("capacity", ((TileEntityMachineCrystallizer) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineCrystallizer) base).tank);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorld().getTileEntity(((TileEntityDummy) te).target);
			if (base instanceof TileEntityMachineGasFlare) {
				NBTTagCompound tag = new NBTTagCompound();
				if (((TileEntityMachineGasFlare) te).tank.getFluidAmount() >= 10) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", 10);
					tag.setDouble("output", 50);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble("output", 0);
				}
				tag.setLong("stored", ((TileEntityMachineGasFlare) te).getSPower());
				tag.setLong("capacity", ((TileEntityMachineGasFlare) te).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineGasFlare) te).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				NBTTagCompound tag = new NBTTagCompound();
				int nrg = 1250;
				int cnsp = 1;
				if (!((TileEntityMachineTurbofan) base).inventory.getStackInSlot(2).isEmpty()) {
					if (((TileEntityMachineTurbofan) base).inventory.getStackInSlot(2).getItem() == ModItems.upgrade_afterburn_1) {
						nrg *= 2;
						cnsp = (int) (cnsp * 2.5D);
					}
					if (((TileEntityMachineTurbofan) base).inventory.getStackInSlot(2).getItem() == ModItems.upgrade_afterburn_2) {
						nrg *= 3;
						cnsp *= 5;
					}
					if (((TileEntityMachineTurbofan) base).inventory.getStackInSlot(2).getItem() == ModItems.upgrade_afterburn_3) {
						nrg *= 4;
						cnsp = (int) (cnsp * 7.5D);
					}
				}
				if (((TileEntityMachineTurbofan) base).tank.getFluidAmount() >= cnsp) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", cnsp);
					tag.setDouble("output", nrg);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble("output", 0);
				}
				tag.setLong("stored", ((TileEntityMachineTurbofan) base).getSPower());
				tag.setLong("capacity", ((TileEntityMachineTurbofan) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineTurbofan) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean("active", ((TileEntityMachineRadGen) base).strength > 0);
				tag.setDouble("output", ((TileEntityMachineRadGen) base).strength);
				tag.setLong("stored", ((TileEntityMachineRadGen) base).getSPower());
				tag.setLong("capacity", ((TileEntityMachineRadGen) base).maxPower);
				tag.setInteger("fuel", ((TileEntityMachineRadGen) base).fuel);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityAMSLimiter) base).getPower());
				tag.setLong("capacity", ((TileEntityAMSLimiter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSLimiter) base).heat);
				FluidInfo.addTank("tank", tag, ((TileEntityAMSLimiter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				NBTTagCompound tag = new NBTTagCompound();
				TileEntityAMSBase reactor = (TileEntityAMSBase) base;
				double output = calcASMBaseOutput(reactor);
				tag.setBoolean("active", output > 0);
				tag.setDouble("output", output);
				tag.setLong("stored", reactor.getSPower());
				tag.setLong("capacity", reactor.maxPower);
				tag.setLong("heat", reactor.heat);
				FluidInfo.addTank("tank", tag, reactor.tanks[0]);
				FluidInfo.addTank("tank2", tag, reactor.tanks[1]);
				FluidInfo.addTank("tank3", tag, reactor.tanks[2]);
				FluidInfo.addTank("tank4", tag, reactor.tanks[3]);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityAMSEmitter) base).getPower());
				tag.setLong("capacity", ((TileEntityAMSEmitter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSEmitter) base).heat);
				FluidInfo.addTank("tank", tag, ((TileEntityAMSEmitter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityMachineOilWell) base).getPower());
				tag.setLong("capacity", ((TileEntityMachineOilWell) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineOilWell) base).tanks[0]);
				FluidInfo.addTank("tank2", tag, ((TileEntityMachineOilWell) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityMachineAssembler) base).getPower());
				tag.setLong("capacity", ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityMachinePumpjack) base).getPower());
				tag.setLong("capacity", ((TileEntityMachinePumpjack) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachinePumpjack) base).tanks[0]);
				FluidInfo.addTank("tank2", tag, ((TileEntityMachinePumpjack) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong("stored", ((TileEntityMachineRefinery) base).getPower());
				tag.setLong("capacity", ((TileEntityMachineRefinery) base).maxPower);
				FluidInfo.addTank("tank", tag, ((TileEntityMachineRefinery) base).tanks[0]);
				FluidInfo.addTank("tank2", tag, ((TileEntityMachineRefinery) base).tanks[1]);
				FluidInfo.addTank("tank3", tag, ((TileEntityMachineRefinery) base).tanks[2]);
				FluidInfo.addTank("tank4", tag, ((TileEntityMachineRefinery) base).tanks[3]);
				FluidInfo.addTank("tank5", tag, ((TileEntityMachineRefinery) base).tanks[4]);
				return tag;
			}
		}
		if (te instanceof TileEntityMachineReactorSmall) {
			TileEntityMachineReactorSmall reactor = (TileEntityMachineReactorSmall) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", reactor.hasCoreHeat());
			double steam = reactor.hullHeat * 1.085D / 100000.0D * reactor.tanks[2].getCapacity() / 50.0D * getSmallReactorConversion(reactor);
			double water = steam;
			if (reactor.tankTypes[2] == ModForgeFluids.steam)
				water /= 100.0D;
			else if (reactor.tankTypes[2] == ModForgeFluids.hotsteam)
				water /= 10.0D;
			tag.setDouble("consumption", (int) Math.ceil(water));
			tag.setDouble("outputmb", (int) Math.floor(steam));
			tag.setLong("core", Math.round(reactor.coreHeat * 2.0E-5D * 980.0D + 20.0D));
			tag.setLong("hull", Math.round(reactor.hullHeat * 1.0E-5D * 980.0D + 20.0D));
			FluidInfo.addTank("tank", tag, reactor.tanks[0]);
			FluidInfo.addTank("tank2", tag, reactor.tanks[1]);
			FluidInfo.addTank("tank3", tag, reactor.tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityReactorHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 2));
				if (core instanceof TileEntityMachineReactorLarge) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityMachineReactorLarge reactor = (TileEntityMachineReactorLarge) core;
					tag.setBoolean("active", reactor.rods > 0 && reactor.fuel > 0);
					tag.setLong("core", Math.round(reactor.coreHeat * 2.0E-5D * 980.0D + 20.0D));
					tag.setLong("hull", Math.round(reactor.hullHeat * 1.0E-5D * 980.0D + 20.0D));
					String fuel = "";
					switch (reactor.type) {
					case URANIUM:
						fuel = "Uranium";
						break;
					case MOX:
						fuel = "MOX";
						break;
					case PLUTONIUM:
						fuel = "Plutonium";
						break;
					case SCHRABIDIUM:
						fuel = "Schrabidium";
						break;
					case THORIUM:
						fuel = "Thorium";
						break;
					default:
						fuel = "ERROR";
						break;
					}
					tag.setString("level", reactor.rods + "%");
					tag.setString("fuelText", fuel + ": " + (reactor.fuel / TileEntityMachineReactorLarge.fuelMult) + "ng");
					tag.setString("depleted", fuel + ": " + (reactor.waste / TileEntityMachineReactorLarge.fuelMult) + "ng");
					double steam = reactor.hullHeat * 1.085D / 100000.0D * 8000.0D / 50.0D * reactor.size;
					double water = steam;
					if (reactor.tankTypes[2] == ModForgeFluids.steam)
						water /= 100.0D;
					else if (reactor.tankTypes[2] == ModForgeFluids.hotsteam)
						water /= 10.0D;
					tag.setDouble("consumption", (int) Math.ceil(water));
					tag.setDouble("outputmb", (int) Math.floor(steam));
					FluidInfo.addTank("tank", tag, reactor.tanks[0]);
					FluidInfo.addTank("tank2", tag, reactor.tanks[1]);
					FluidInfo.addTank("tank3", tag, reactor.tanks[2]);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityWatzCore reactor = (TileEntityWatzCore) core;
					tag.setBoolean("active", reactor.powerList > 0);
					tag.setLong("stored", reactor.getSPower());
					tag.setLong("capacity", reactor.maxPower);
					tag.setDouble("output", reactor.powerList);
					tag.setLong("heat", reactor.heatList);
					FluidInfo.addTank("tank", tag, reactor.tank);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityFusionMultiblock reactor = (TileEntityFusionMultiblock) core;
					boolean active = reactor.isRunning();
					tag.setBoolean("active", active);
					tag.setLong("stored", reactor.getSPower());
					tag.setLong("capacity", reactor.maxPower);
					tag.setDouble("consumption", active ? 20 : 0);
					tag.setDouble("output", active ? reactor.isCoatingValid(te.getWorld()) ? 200000 : 100000 : 0);
					FluidInfo.addTank("tank", tag, reactor.tanks[0]);
					FluidInfo.addTank("tank2", tag, reactor.tanks[1]);
					FluidInfo.addTank("tank3", tag, reactor.tanks[2]);
					return tag;
				}
			}
		}
		return null;
	}

	private double calcAmgenOutput(World world, Block block, BlockPos pos) {
		long power = 0;
		if (block == ModBlocks.machine_amgen) {
			RadiationSavedData data = RadiationSavedData.getData(world);
			float rad = data.getRadNumFromCoord(pos);
			power = (long) ((float) power + rad);
			RadiationSavedData.decrementRad(world, pos, 5.0F);
		} else if (block == ModBlocks.machine_geo) {
			Block b = world.getBlockState(pos.down()).getBlock();
			if (b == ModBlocks.geysir_water) {
				power += 75L;
			} else if (b == ModBlocks.geysir_chlorine) {
				power += 100L;
			} else if (b == ModBlocks.geysir_vapor) {
				power += 50L;
			} else if (b == ModBlocks.geysir_nether) {
				power += 500L;
			} else if (b == Blocks.LAVA) {
				power += 100L;
				if (world.rand.nextInt(1200) == 0)
					world.setBlockState(pos.down(), Blocks.OBSIDIAN.getDefaultState());
			} else if (b == Blocks.FLOWING_LAVA) {
				power += 25L;
				if (world.rand.nextInt(600) == 0)
					world.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState());
			}
			b = world.getBlockState(pos.up()).getBlock();
			if (b == Blocks.LAVA) {
				power += 100L;
				if (world.rand.nextInt(1200) == 0)
					world.setBlockState(pos.up(), Blocks.OBSIDIAN.getDefaultState());
			} else if (b == Blocks.FLOWING_LAVA) {
				power += 25L;
				if (world.rand.nextInt(600) == 0)
					world.setBlockState(pos.up(), Blocks.COBBLESTONE.getDefaultState());
			}
		}
		return power;
	}

	private double calcIGeneratorOutput(TileEntityMachineIGenerator te) {
		double output = 0;
		int rtg = 0;
		int temp = 0;
		for (int i = 3; i <= 5; i++)
			if (te.inventory.getStackInSlot(i).getItem() == ModItems.thermo_element)
				rtg += 15;
		for (int i = 0; i < te.pellets.length; i++)
			if (te.pellets[i] != null)
				temp += (te.pellets[i]).heat;
		output += Math.min(te.temperature + temp, rtg) + te.torque * 0.025D;
		return output;
	}

	private long calcASMBaseOutput(TileEntityAMSBase te) {
		int booster = 0;
		if (te.mode == 2) {
			booster += getBooster(te.getWorld(), te.getPos().add(6, 0, 0), 4);
			booster += getBooster(te.getWorld(), te.getPos().add(-6, 0, 0), 5);
			booster += getBooster(te.getWorld(), te.getPos().add(0, 0, 6), 2);
			booster += getBooster(te.getWorld(), te.getPos().add(0, 0, -6), 3);
		}
		float powerMod = 1.0F;
		/*float heatMod = 1.0F;
		int heatBase = ItemAMSCore.getHeatBase(te.inventory.getStackInSlot(12));*/
		long powerBase = ItemAMSCore.getPowerBase(te.inventory.getStackInSlot(12));
		for (int j = 8; j < 12; j++) {
			powerMod *= ItemCatalyst.getPowerMod(te.inventory.getStackInSlot(j));
			//heatMod *= ItemCatalyst.getHeatMod(te.inventory.getStackInSlot(j));
		}
		powerBase *= te.efficiency;
		powerBase = (long) (powerBase * Math.pow(1.25D, booster));
		/*heatBase = (int) (heatBase * Math.pow(1.25D, booster));
		heatBase *= 100 - te.field;*/
		if (getFuelPower(te.tanks[2].getFluid()) > 0 && getFuelPower(te.tanks[3].getFluid()) > 0 && te.tanks[2].getFluidAmount() > 0 && te.tanks[3].getFluidAmount() > 0)
			return (long) ((float) powerBase * powerMod * gauss(1.0F, ((te.heat /*- heatBase * heatMod / te.field / 100.0F*/ - 2500) / 5000)) / 1000.0F * getFuelPower(te.tanks[2].getFluid()) * getFuelPower(te.tanks[3].getFluid()));
		return 0;
	}

	private int getBooster(World world, BlockPos pos, int meta) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityAMSLimiter)
			if (!((TileEntityAMSLimiter) te).locked && te.getBlockMetadata() == meta)
				return 1;
		return 0;
	}

	private int getFuelPower(FluidStack type) {
		if (type == null)
			return 0;
		if (type.getFluid() == ModForgeFluids.deuterium)
			return 50;
		if (type.getFluid() == ModForgeFluids.tritium)
			return 75;
		return 0;
	}

	private float gauss(float a, float x) {
		double amplifier = 0.1D;
		return (float) (1.0D / Math.sqrt(a * Math.PI) * Math.pow(Math.E, -1.0D * Math.pow(x, 2.0D) / amplifier));
	}

	private double getSmallReactorConversion(TileEntityMachineReactorSmall te) {
		double conversion = 1.0D;
		conversion *= getInteractionForBlock(te.getWorld(), te.getPos().add(1, 1, 0));
		conversion *= getInteractionForBlock(te.getWorld(), te.getPos().add(-1, 1, 0));
		conversion *= getInteractionForBlock(te.getWorld(), te.getPos().add(0, 1, 1));
		conversion *= getInteractionForBlock(te.getWorld(), te.getPos().add(0, 1, -1));
		return conversion;
	}

	private double getInteractionForBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		TileEntity te = world.getTileEntity(pos);
		double conversion = 1.0D;
		if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
			conversion *= 0.5D;
		else if (block == Blocks.REDSTONE_BLOCK)
			conversion *= 1.15D;
		else if (block == ModBlocks.block_beryllium)
			conversion *= 1.05D;
		else if (block == ModBlocks.block_schrabidium)
			conversion *= 1.25D;
		return conversion;
	}
	
	private Fluid getFluid(FluidTank tank, ItemStack stack) {
		Fluid fluid = null;
		if (tank.getFluid() != null)
			fluid = tank.getFluid().getFluid();
		if (fluid != null)
			return fluid;
		if (!stack.isEmpty()) {
			if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				IFluidHandlerItem handler = (IFluidHandlerItem) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if (handler != null)
					fluid = FluidUtil.getFluidContained(stack).getFluid();
			} else if (stack.getItem() instanceof IItemFluidHandler) {
				IItemFluidHandler handler = (IItemFluidHandler) stack.getItem();
				FluidStack contained = handler.drain(stack, 2147483647, false);
				fluid = contained.getFluid();
			} else if (stack.hasTagCompound()) {
				FluidStack contained = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag("HbmFluidKey"));
				if (contained != null)
					fluid = contained.getFluid();
			}
		}
		return fluid;
	}

	@Override
	public void loadOreInfo() {
		loadOre(1, 32, 30, 10, ModBlocks.gas_flammable);
		loadOre(1, 32, 30, 10, ModBlocks.gas_explosive);
		loadOre(25, 6, 30, 10, ModBlocks.ore_gneiss_iron);
		loadOre(10, 6, 30, 10, ModBlocks.ore_gneiss_gold);
		loadOre(WorldConfig.uraniumSpawn * 3, 6, 30, 10, ModBlocks.ore_gneiss_uranium);
		loadOre(WorldConfig.copperSpawn * 3, 6, 30, 10, ModBlocks.ore_gneiss_copper);
		loadOre(WorldConfig.asbestosSpawn * 3, 6, 30, 10, ModBlocks.ore_gneiss_asbestos);
		loadOre(WorldConfig.lithiumSpawn, 6, 30, 10, ModBlocks.ore_gneiss_lithium);
		loadOre(WorldConfig.rareSpawn, 6, 30, 10, ModBlocks.ore_gneiss_asbestos);
		loadOre(WorldConfig.gassshaleSpawn * 3, 10, 30, 10, ModBlocks.ore_gneiss_gas);
		loadOre(WorldConfig.uraniumSpawn, 5, 5, 20, ModBlocks.ore_uranium);
		loadOre(WorldConfig.thoriumSpawn, 5, 5, 25, ModBlocks.ore_thorium);
		loadOre(WorldConfig.titaniumSpawn, 6, 5, 30, ModBlocks.ore_titanium);
		loadOre(WorldConfig.sulfurSpawn, 8, 5, 30, ModBlocks.ore_sulfur);
		loadOre(WorldConfig.aluminiumSpawn, 6, 5, 40, ModBlocks.ore_aluminium);
		loadOre(WorldConfig.copperSpawn, 6, 5, 45, ModBlocks.ore_copper);
		loadOre(WorldConfig.fluoriteSpawn, 4, 5, 45, ModBlocks.ore_fluorite);
		loadOre(WorldConfig.niterSpawn, 6, 5, 30, ModBlocks.ore_niter);
		loadOre(WorldConfig.tungstenSpawn, 8, 5, 30, ModBlocks.ore_tungsten);
		loadOre(WorldConfig.leadSpawn, 9, 5, 30, ModBlocks.ore_lead);
		loadOre(WorldConfig.berylliumSpawn, 4, 5, 30, ModBlocks.ore_beryllium);
		loadOre(WorldConfig.rareSpawn, 5, 5, 20, ModBlocks.ore_rare);
		loadOre(WorldConfig.ligniteSpawn, 24, 35, 25, ModBlocks.ore_lignite);
		loadOre(WorldConfig.asbestosSpawn, 4, 16, 16, ModBlocks.ore_asbestos);
		loadOre(WorldConfig.cinnebarSpawn, 4, 8, 16, ModBlocks.ore_cinnebar);
		loadOre(WorldConfig.cobaltSpawn, 4, 4, 8, ModBlocks.ore_cobalt);
		loadOre(WorldConfig.ironClusterSpawn, 6, 5, 50, ModBlocks.cluster_iron);
		loadOre(WorldConfig.titaniumClusterSpawn, 6, 5, 30, ModBlocks.cluster_titanium);
		loadOre(WorldConfig.aluminiumClusterSpawn, 6, 5, 40, ModBlocks.cluster_aluminium);
		loadOre(1, 64, 32, 32, ModBlocks.ore_coal_oil);
	}

	private void loadOre(int veinCount, int amount, int minHeight, int variance, Block block) {
		EnergyControl.oreHelper.put(OreHelper.getId(block, 0), new OreHelper(minHeight, minHeight + variance, amount, veinCount));
	}
}
