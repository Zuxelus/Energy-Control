package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidTank;
import com.hbm.items.machine.ItemBattery;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.oil.*;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.hbm.tileentity.machine.storage.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHBM;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitHBM;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

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
		long amount =  Math.min(Math.min((long) needed, item.getDischargeRate()), item.getCharge(stack));
		item.dischargeBattery(stack, amount);
		return amount;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("euType", "HE");
		if (te instanceof TileEntityMachineBattery) {
			tag.setDouble("storage", ((TileEntityMachineBattery) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			tag.setDouble("storage", ((TileEntityMachineCoal) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineCoal) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble("storage", ((TileEntityMachineDiesel) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineDiesel) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			tag.setDouble("storage", ((TileEntityMachineSeleniumEngine) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineSeleniumEngine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble("storage", ((TileEntityMachineRTG) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble("storage", ((TileEntityMachineMiniRTG) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			tag.setDouble("storage", ((TileEntityMachineAmgen) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			tag.setDouble("storage", ((TileEntityMachineSPP) te).getPower());
			tag.setDouble("maxStorage", ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			tag.setDouble("storage", ((TileEntityMachineTurbine) te).getPower());
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
				tag.setDouble("storage", ((TileEntityMachineLargeTurbine) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineLargeTurbine) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				tag.setDouble("storage", ((TileEntityMachineIGenerator) base).getPower());
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
			if (base instanceof TileEntityChungus) {
				tag.setDouble("storage", ((TileEntityChungus) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityChungus) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineGasFlare) {
				tag.setDouble("storage", ((TileEntityMachineGasFlare) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineGasFlare) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				tag.setDouble("storage", ((TileEntityMachineTurbofan) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineTurbofan) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setDouble("storage", ((TileEntityMachineRadGen) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineRadGen) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				tag.setDouble("storage", ((TileEntityAMSLimiter) base).power);
				tag.setDouble("maxStorage", ((TileEntityAMSLimiter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				tag.setDouble("storage", ((TileEntityAMSBase) base).power);
				tag.setDouble("maxStorage", ((TileEntityAMSBase) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				tag.setDouble("storage", ((TileEntityAMSEmitter) base).power);
				tag.setDouble("maxStorage", ((TileEntityAMSEmitter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setDouble("storage", ((TileEntityMachineOilWell) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineOilWell) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				tag.setDouble("storage", ((TileEntityMachineAssembler) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setDouble("storage", ((TileEntityMachinePumpjack) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachinePumpjack) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				tag.setDouble("storage", ((TileEntityMachineRefinery) base).getPower());
				tag.setDouble("maxStorage", ((TileEntityMachineRefinery) base).maxPower);
				return tag;
			}
		}
		/*if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorldObj().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					tag.setDouble("storage", ((TileEntityWatzCore) core).getSPower());
					tag.setDouble("maxStorage", ((TileEntityWatzCore) core).maxPower);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorldObj().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					tag.setDouble("storage", ((TileEntityFusionMultiblock) core).getSPower());
					tag.setDouble("maxStorage", ((TileEntityFusionMultiblock) core).maxPower);
					return tag;
				}
			}
		}*/
		return null;
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
		/*if (te instanceof TileEntityMachineReactorSmall)
			return (int) Math.round(((TileEntityMachineReactorSmall) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);
		if (te instanceof TileEntityMachineReactorLarge)
			return (int) Math.round(((TileEntityMachineReactorLarge) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);*/
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileEntityMachineCoal) {
			result.add(toFluidInfo(((TileEntityMachineCoal) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineDiesel) {
			result.add(toFluidInfo(((TileEntityMachineDiesel) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			result.add(toFluidInfo(((TileEntityMachineSeleniumEngine) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachineTurbine) {
			result.add(toFluidInfo(((TileEntityMachineTurbine) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineTurbine) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineBoiler) {
			result.add(toFluidInfo(((TileEntityMachineBoiler) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineBoiler) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			result.add(toFluidInfo(((TileEntityMachineBoilerElectric) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineBoilerElectric) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityMachineGasCent) {
			result.add(toFluidInfo(((TileEntityMachineGasCent) te).tank));
			return result;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				result.add(toFluidInfo(((TileEntityMachineLargeTurbine) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineLargeTurbine) base).tanks[1]));
				return result;
			}
			/*if (base instanceof TileEntitySolarBoiler) {
				IFluidTankProperties tanks[] = ((TileEntitySolarBoiler) base).getTankProperties();
				result.add(toFluidInfo(tanks[0].getContents(), tanks[0].getCapacity()));
				result.add(toFluidInfo(tanks[1].getContents(), tanks[1].getCapacity()));
				return result;
			}*/
			if (base instanceof TileEntityMachineIGenerator) {
				result.add(toFluidInfo(((TileEntityMachineIGenerator) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineIGenerator) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachineIGenerator) base).tanks[2]));
				return result;
			}
			if (base instanceof TileEntityITER) {
				result.add(toFluidInfo(((TileEntityITER) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityITER) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityITER) base).plasma));
				return result;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				result.add(toFluidInfo(((TileEntityMachinePlasmaHeater) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachinePlasmaHeater) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachinePlasmaHeater) base).plasma));
				return result;
			}
			if (base instanceof TileEntityMachineOrbus) {
				result.add(toFluidInfo(((TileEntityMachineOrbus) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineFractionTower) {
				result.add(toFluidInfo(((TileEntityMachineFractionTower) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineFractionTower) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachineFractionTower) base).tanks[2]));
				return result;
			}
			if (base instanceof TileEntityMachineCrystallizer) {
				result.add(toFluidInfo(((TileEntityMachineCrystallizer) base).tank));
				return result;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineFluidTank) {
				result.add(toFluidInfo(((TileEntityMachineFluidTank) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineGasFlare) {
				result.add(toFluidInfo(((TileEntityMachineGasFlare) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				result.add(toFluidInfo(((TileEntityMachineTurbofan) base).tank));
				return result;
			}
			if (base instanceof TileEntityAMSLimiter) {
				result.add(toFluidInfo(((TileEntityAMSLimiter) base).tank));
				return result;
			}
			if (base instanceof TileEntityAMSBase) {
				result.add(toFluidInfo(((TileEntityAMSBase) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityAMSBase) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityAMSBase) base).tanks[2]));
				result.add(toFluidInfo(((TileEntityAMSBase) base).tanks[3]));
				return result;
			}
			if (base instanceof TileEntityAMSEmitter) {
				result.add(toFluidInfo(((TileEntityAMSEmitter) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineOilWell) {
				result.add(toFluidInfo(((TileEntityMachineOilWell) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineOilWell) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				result.add(toFluidInfo(((TileEntityMachinePumpjack) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachinePumpjack) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntityMachineRefinery) {
				result.add(toFluidInfo(((TileEntityMachineRefinery) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineRefinery) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachineRefinery) base).tanks[2]));
				result.add(toFluidInfo(((TileEntityMachineRefinery) base).tanks[3]));
				result.add(toFluidInfo(((TileEntityMachineRefinery) base).tanks[4]));
				return result;
			}
		}
		/*if (te instanceof TileEntityMachineReactorSmall) {
			result.add(toFluidInfo(((TileEntityMachineReactorSmall) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineReactorSmall) te).tanks[1]));
			result.add(toFluidInfo(((TileEntityMachineReactorSmall) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityReactorHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 2));
				if (core instanceof TileEntityMachineReactorLarge) {
					result.add(toFluidInfo(((TileEntityMachineReactorLarge) core).tanks[0]));
					result.add(toFluidInfo(((TileEntityMachineReactorLarge) core).tanks[1]));
					result.add(toFluidInfo(((TileEntityMachineReactorLarge) core).tanks[2]));
					return result;
				}
			}
		}*/
		/*if (te instanceof TileEntityWatzHatch) {
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
		}*/
		return null;
	}

	private static FluidInfo toFluidInfo(FluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private void addTank(String name, NBTTagCompound tag, FluidTank tank) {
		if (tank.getFill() == 0)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("stored", ((TileEntityMachineBattery) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", ((TileEntityMachineCoal) te).burnTime > 0);
			tag.setLong("stored", ((TileEntityMachineCoal) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineCoal) te).maxPower);
			if (((TileEntityMachineCoal) te).burnTime > 0)
				tag.setDouble("output", 25);
			else
				tag.setDouble("output", 0);
			addTank("tank", tag, ((TileEntityMachineCoal) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineDiesel diesel = ((TileEntityMachineDiesel) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFill() > 0) {
				tag.setBoolean("active", true);
				tag.setDouble("output", 10);
				tag.setDouble("output", diesel.getHEFromFuel());
			} else {
				tag.setBoolean("active", false);
				tag.setDouble("output", 0);
				tag.setDouble("output", 0);
			}
			tag.setLong("stored", ((TileEntityMachineDiesel) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineDiesel) te).maxPower);
			addTank("tank", tag, ((TileEntityMachineDiesel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineSeleniumEngine diesel = ((TileEntityMachineSeleniumEngine) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFill() > 0 && diesel.pistonCount > 2) {
				tag.setBoolean("active", true);
				tag.setDouble("output", diesel.getHEFromFuel() * Math.pow(diesel.pistonCount, 1.15D));
			} else {
				tag.setBoolean("active", false);
				tag.setDouble("output", 0);
			}
			tag.setLong("stored", ((TileEntityMachineSeleniumEngine) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineSeleniumEngine) te).maxPower);
			addTank("tank", tag, ((TileEntityMachineSeleniumEngine) te).tank);
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
			tag.setLong("stored", ((TileEntityMachineRTG) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", true);
			tag.setDouble("output", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 70);
			tag.setLong("stored", ((TileEntityMachineMiniRTG) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			NBTTagCompound tag = new NBTTagCompound();
			/*double output = calcAmgenOutput(te.getWorldObj(), te.getBlockType(), te.getPos());
			tag.setBoolean("active", output > 0);
			tag.setDouble("output", output);*/
			tag.setLong("stored", ((TileEntityMachineAmgen) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = Math.max(((TileEntityMachineSPP) te).checkStructure() * 15, 0);
			tag.setBoolean("active", output > 0);
			tag.setDouble("output", output);
			tag.setLong("stored", ((TileEntityMachineSPP) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine turbine = (TileEntityMachineTurbine) te;
			NBTTagCompound tag = new NBTTagCompound();
			/*ArrayList values = HBMHooks.map.get(turbine);
			if (values != null) {
				tag.setBoolean("active", (int) values.get(1) > 0);
				tag.setDouble("consumption", (int) values.get(0));
				tag.setDouble("output", (int) values.get(1));
				tag.setDouble("outputmb", (int) values.get(2));
			}*/
			tag.setLong("stored", ((TileEntityMachineTurbine) te).getPower());
			tag.setLong("capacity", ((TileEntityMachineTurbine) te).maxPower);
			/*FluidInfo.addTank("tank", tag, ((TileEntityMachineTurbine) te).tanks[0]);
			FluidInfo.addTank("tank2", tag, ((TileEntityMachineTurbine) te).tanks[1]);*/
			return tag;
		}
		if (te instanceof TileEntityReactorResearch) {
			
		}
		return null;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitHBM::new);
		ItemCardMain.register(ItemCardHBM::new);
	}

	@Override
	public void loadRecipes() {

	}
}
