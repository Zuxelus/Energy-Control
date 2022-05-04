package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidTank;
import com.hbm.inventory.recipes.FusionRecipes;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.TileEntityProxyEnergy;
import com.hbm.tileentity.TileEntityProxyInventory;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.TileEntityMachineGasCent.PseudoFluidTank;
import com.hbm.tileentity.machine.oil.*;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBase;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKRod;
import com.hbm.tileentity.machine.storage.*;
import com.hbm.util.ContaminationUtil;
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

import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
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
		tag.setString(DataHelper.EUTYPE, "HE");
		if (te instanceof TileEntityMachineBattery) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSeleniumEngine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSeleniumEngine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).maxPower);
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
		if (te instanceof TileEntityMachineBoilerElectric) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBoilerElectric) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineArcFurnace) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineArcFurnace) te).maxPower);
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
		if (te instanceof TileEntityMachineEPress) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineIGenerator) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineIGenerator) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityITER) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityITER) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePlasmaHeater) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePlasmaHeater) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityChungus) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityChungus) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityChungus) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineGasFlare) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasFlare) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbofan) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSLimiter) base).power);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSLimiter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSBase) base).power);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSBase) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSEmitter) base).power);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSEmitter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAssembler) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePumpjack) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityProxyEnergy) {
			TileEntity base = ((TileEntityProxyEnergy) te).getTE();
			if (base instanceof TileEntityFEL) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityFEL) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityFEL) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityProxyInventory) {
			TileEntity base = ((TileEntityProxyInventory) te).getTE();
			if (base instanceof TileEntityMachineCrystallizer) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).maxPower);
				return tag;
			}
		}
		/*if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorldObj().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					tag.setDouble(DataHelper.ENERGY, ((TileEntityWatzCore) core).getSPower());
					tag.setDouble(DataHelper.CAPACITY, ((TileEntityWatzCore) core).maxPower);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorldObj().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionMultiblock) core).getSPower());
					tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionMultiblock) core).maxPower);
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
			result.add(toFluidInfo(((TileEntityMachineGasCent) te).inputTank));
			result.add(toFluidInfo(((TileEntityMachineGasCent) te).outputTank));
			return result;
		}
		if (te instanceof TileEntityMachineUF6Tank) {
			result.add(toFluidInfo(((TileEntityMachineUF6Tank) te).tank));
			return result;
		}
		if (te instanceof TileEntityMachinePuF6Tank) {
			result.add(toFluidInfo(((TileEntityMachinePuF6Tank) te).tank));
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
		if (te instanceof TileEntityProxyInventory) {
			TileEntity base = ((TileEntityProxyInventory) te).getTE();
			if (base instanceof TileEntityMachineCrystallizer) {
				result.add(toFluidInfo(((TileEntityMachineCrystallizer) base).tank));
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

	private static FluidInfo toFluidInfo(PseudoFluidTank tank) {
		return new FluidInfo(tank.getTankType().getName(), tank.getFill(), tank.getMaxFill());
	}

	private void addTank(String name, NBTTagCompound tag, FluidTank tank) {
		if (tank.getFill() == 0)
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
		if (tank.getFill() == 0)
			tag.setString(name, "N/A");
		else
			tag.setString(name, String.format("%s: %s mB", tank.getTankType().getName(), tank.getFill()));
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).maxPower);
			ArrayList values = HBMHooks.map.get(te);
			if (values != null)
				tag.setLong("diff", (Long) values.get(0) - (Long) values.get(1));
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", ((TileEntityMachineCoal) te).burnTime > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).maxPower);
			if (((TileEntityMachineCoal) te).burnTime > 0)
				tag.setDouble(DataHelper.OUTPUT, 25);
			else
				tag.setDouble(DataHelper.OUTPUT, 0);
			addTank("tank", tag, ((TileEntityMachineCoal) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineDiesel diesel = ((TileEntityMachineDiesel) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFill() > 0) {
				tag.setBoolean("active", true);
				tag.setDouble(DataHelper.OUTPUT, 10);
				tag.setDouble(DataHelper.OUTPUT, diesel.getHEFromFuel());
			} else {
				tag.setBoolean("active", false);
				tag.setDouble(DataHelper.OUTPUT, 0);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).maxPower);
			addTank("tank", tag, ((TileEntityMachineDiesel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineSeleniumEngine diesel = ((TileEntityMachineSeleniumEngine) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFill() > 0 && diesel.pistonCount > 2) {
				tag.setBoolean("active", true);
				tag.setDouble(DataHelper.CONSUMPTION, diesel.pistonCount);
				tag.setDouble(DataHelper.OUTPUT, (long) (diesel.getHEFromFuel() * Math.pow(diesel.pistonCount, 1.15D)));
			} else {
				tag.setBoolean("active", false);
				tag.setDouble(DataHelper.CONSUMPTION, diesel.pistonCount);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, diesel.getPower());
			tag.setLong(DataHelper.CAPACITY, diesel.maxPower);
			tag.setInteger("pistons", diesel.pistonCount);
			addTank("tank", tag, diesel.tank);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			NBTTagCompound tag = new NBTTagCompound();

			if (((TileEntityMachineRTG) te).heat > 0) {
				tag.setBoolean("active", true);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineRTG) te).heat);
			} else {
				tag.setBoolean("active", false);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", true);
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 70);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			NBTTagCompound tag = new NBTTagCompound();
			/*double output = calcAmgenOutput(te.getWorldObj(), te.getBlockType(), te.getPos());
			tag.setBoolean("active", output > 0);
			tag.setDouble(DataHelper.OUTPUT, output);*/
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = Math.max(((TileEntityMachineSPP) te).checkStructure() * 15, 0);
			tag.setBoolean("active", output > 0);
			tag.setDouble(DataHelper.OUTPUT, output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine turbine = (TileEntityMachineTurbine) te;
			NBTTagCompound tag = new NBTTagCompound();
			/*ArrayList values = HBMHooks.map.get(turbine);
			if (values != null) {
				tag.setBoolean("active", (int) values.get(1) > 0);
				tag.setDouble("consumption", (int) values.get(0));
				tag.setDouble(DataHelper.OUTPUT, (int) values.get(1));
				tag.setDouble("outputmb", (int) values.get(2));
			}*/
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).maxPower);
			addTank("tank", tag, ((TileEntityMachineTurbine) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineTurbine) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineShredder) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineShredder) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("hull", ((TileEntityMachineBoiler) te).heat / 100);
			addTank("tank", tag, ((TileEntityMachineBoiler) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBoilerElectric) te).maxPower);
			tag.setLong("hull", ((TileEntityMachineBoilerElectric) te).heat / 100);
			addTank("tank", tag, ((TileEntityMachineBoilerElectric) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineBoilerElectric) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineArcFurnace) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineArcFurnace) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCentrifuge) te).progress > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCentrifuge) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCentrifuge) te).maxPower);
			tag.setInteger("progress", ((TileEntityMachineCentrifuge) te).progress);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineGasCent) te).progress > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasCent) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasCent) te).maxPower);
			tag.setInteger("progress", ((TileEntityMachineGasCent) te).progress);
			addTank("tank", tag, ((TileEntityMachineGasCent) te).tank);
			addTank("tank2", tag, ((TileEntityMachineGasCent) te).inputTank);
			addTank("tank3", tag, ((TileEntityMachineGasCent) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityMachineEPress) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).maxPower);
			tag.setInteger("progress", ((TileEntityMachineEPress) te).progress);
			return tag;
		}
		if (te instanceof TileEntityDiFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("energy_", ((TileEntityDiFurnace) te).dualPower);
			tag.setLong("capacity_", ((TileEntityDiFurnace) te).maxPower);
			tag.setInteger("progress", ((TileEntityDiFurnace) te).dualCookTime);
			return tag;
		}
		if (te instanceof TileEntityDiFurnaceRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("fuel", ((TileEntityDiFurnaceRTG) te).getPower());
			tag.setInteger("progress", ((TileEntityDiFurnaceRTG) te).progress);
			return tag;
		}
		if (te instanceof TileEntityMachineUF6Tank) {
			NBTTagCompound tag = new NBTTagCompound();
			addTank("tank", tag, ((TileEntityMachineUF6Tank) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachinePuF6Tank) {
			NBTTagCompound tag = new NBTTagCompound();
			addTank("tank", tag, ((TileEntityMachinePuF6Tank) te).tank);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				ArrayList values = HBMHooks.map.get(base);
				if (values != null) {
					tag.setBoolean("active", (int) values.get(1) > 0);
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(1));
					tag.setDouble("outputmb", (int) values.get(2));
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).maxPower);
				addTank("tank", tag, ((TileEntityMachineLargeTurbine) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineLargeTurbine) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntitySolarBoiler) {
				/*IFluidTankProperties tanks[] = ((TileEntitySolarBoiler) base).getTankProperties();
				addTank("tank", tag, tanks[0].getContents());
				addTank("tank2", tag, tanks[1].getContents());*/
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				TileEntityMachineIGenerator generator = (TileEntityMachineIGenerator) base;
				int output = 0;
				ArrayList values = HBMHooks.map.get(base);
				if (values != null)
					output = (int) values.get(0);
				tag.setBoolean("active", output > 0);
				tag.setDouble(DataHelper.OUTPUT, output);
				tag.setLong(DataHelper.ENERGY, generator.getPower());
				tag.setLong(DataHelper.CAPACITY, generator.maxPower);
				/*tag.setInteger("temp", generator.temperature + 300);
				tag.setInteger("speed", generator.torque);*/
				addTank("tank", tag, generator.tanks[0]);
				addTank("tank2", tag, generator.tanks[1]);
				addTank("tank3", tag, generator.tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				TileEntityITER reactor = (TileEntityITER) base;
				tag.setLong(DataHelper.ENERGY, reactor.getPower());
				tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
				/*int output = FusionRecipes.getSteamProduction(reactor.plasmaType) * 20;
				tag.setDouble("consumption", output * 10);
				tag.setDouble("outputmb", output);
				addTank("tank", tag, reactor.tanks[0]);
				addTank("tank2", tag, reactor.tanks[1]);
				addTank("tank3", tag, reactor.plasma);*/
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				TileEntityMachinePlasmaHeater heater = (TileEntityMachinePlasmaHeater) base;
				tag.setLong(DataHelper.ENERGY, heater.getPower());
				tag.setLong(DataHelper.CAPACITY, heater.maxPower);
				addTank("tank", tag, heater.tanks[0]);
				addTank("tank2", tag, heater.tanks[1]);
				addTank("tank3", tag, heater.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachineOrbus) {
				addTank("tank", tag, ((TileEntityMachineOrbus) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineFractionTower) {
				addTank("tank", tag, ((TileEntityMachineFractionTower) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineFractionTower) base).tanks[1]);
				addTank("tank3", tag, ((TileEntityMachineFractionTower) base).tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityTowerLarge) {
				ArrayList values = HBMHooks.map.get(base);
				if (values != null) {
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble("outputmb", (int) values.get(0));
				}
				addTank("tank", tag, ((TileEntityTowerLarge) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityTowerLarge) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityChungus) {
				TileEntityChungus engine = (TileEntityChungus) base;
				ArrayList values = HBMHooks.map.get(engine);
				if (values != null) {
					tag.setBoolean("active", (int) values.get(1) > 0);
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(1));
					tag.setDouble("outputmb", (int) values.get(2));
				}
				tag.setLong(DataHelper.ENERGY, engine.getPower());
				tag.setLong(DataHelper.CAPACITY, engine.maxPower);
				addTank("tank", tag, engine.tanks[0]);
				addTank("tank2", tag, engine.tanks[1]);
				return tag;
			}
			if (base instanceof TileEntitySILEX) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySILEX) base).progress > 0);
				tag.setInteger("progress", ((TileEntitySILEX) base).progress);
				addTank("tank", tag, ((TileEntitySILEX) base).tank);
				if (((TileEntitySILEX) base).current == null)
					tag.setString("tank2", "N/A");
				else
					tag.setString("tank2", String.format("%s: %s mB", ((TileEntitySILEX) base).current.toStack().getDisplayName(), ((TileEntitySILEX) base).currentFill));
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineGasFlare) {
				NBTTagCompound tag = new NBTTagCompound();
				if (((TileEntityMachineGasFlare) te).tank.getFill() >= 10) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", 10);
					tag.setDouble(DataHelper.OUTPUT, 50);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasFlare) te).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) te).maxPower);
				addTank("tank", tag, ((TileEntityMachineGasFlare) te).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				NBTTagCompound tag = new NBTTagCompound();
				int nrg = 1250;
				int cnsp = 1;
				/*if (!((TileEntityMachineTurbofan) base).inventory.getStackInSlot(2).isEmpty()) {
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
				if (((TileEntityMachineTurbofan) base).tank.getFill() >= cnsp) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", cnsp);
					tag.setDouble(DataHelper.OUTPUT, nrg);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}*/
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbofan) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) base).maxPower);
				addTank("tank", tag, ((TileEntityMachineTurbofan) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				NBTTagCompound tag = new NBTTagCompound();
				/*tag.setBoolean("active", ((TileEntityMachineRadGen) base).strength > 0);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineRadGen) base).strength);*/
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).maxPower);
				//tag.setInteger("fuel", ((TileEntityMachineRadGen) base).fuel);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				NBTTagCompound tag = new NBTTagCompound();
				//tag.setLong(DataHelper.ENERGY, ((TileEntityAMSLimiter) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSLimiter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSLimiter) base).heat);
				addTank("tank", tag, ((TileEntityAMSLimiter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				NBTTagCompound tag = new NBTTagCompound();
				TileEntityAMSBase reactor = (TileEntityAMSBase) base;
				/*double output = calcASMBaseOutput(reactor);
				tag.setBoolean("active", output > 0);
				tag.setDouble(DataHelper.OUTPUT, output);
				tag.setLong(DataHelper.ENERGY, reactor.getPower());*/
				tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
				tag.setLong("heat", reactor.heat);
				addTank("tank", tag, reactor.tanks[0]);
				addTank("tank2", tag, reactor.tanks[1]);
				addTank("tank3", tag, reactor.tanks[2]);
				addTank("tank4", tag, reactor.tanks[3]);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				NBTTagCompound tag = new NBTTagCompound();
				//tag.setLong(DataHelper.ENERGY, ((TileEntityAMSEmitter) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSEmitter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSEmitter) base).heat);
				addTank("tank", tag, ((TileEntityAMSEmitter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				//tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).maxPower);
				addTank("tank", tag, ((TileEntityMachineOilWell) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineOilWell) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssembler) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePumpjack) base).getPower());
				//tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).maxPower);
				addTank("tank", tag, ((TileEntityMachinePumpjack) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachinePumpjack) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).maxPower);
				addTank("tank", tag, ((TileEntityMachineRefinery) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineRefinery) base).tanks[1]);
				addTank("tank3", tag, ((TileEntityMachineRefinery) base).tanks[2]);
				addTank("tank4", tag, ((TileEntityMachineRefinery) base).tanks[3]);
				addTank("tank5", tag, ((TileEntityMachineRefinery) base).tanks[4]);
				return tag;
			}
		}
		if (te instanceof TileEntityProxyEnergy) {
			TileEntity base = ((TileEntityProxyEnergy) te).getTE();
			if (base instanceof TileEntityFEL) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityFEL) base).isOn);
				tag.setLong(DataHelper.ENERGY, ((TileEntityFEL) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityFEL) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityProxyInventory) {
			TileEntity base = ((TileEntityProxyInventory) te).getTE();
			if (base instanceof TileEntityMachineCrystallizer) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).maxPower);
				tag.setInteger("progress", ((TileEntityMachineCrystallizer) base).progress);
				addTank("tank", tag, ((TileEntityMachineCrystallizer) base).tank);
				return tag;
			}
		}
		/*if (te instanceof TileEntityMachineReactorSmall) {
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
			addTank("tank", tag, reactor.tanks[0]);
			addTank("tank2", tag, reactor.tanks[1]);
			addTank("tank3", tag, reactor.tanks[2]);
			return tag;
		}*/
		/*if (te instanceof TileEntityReactorHatch) {
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
					addTank("tank", tag, reactor.tanks[0]);
					addTank("tank2", tag, reactor.tanks[1]);
					addTank("tank3", tag, reactor.tanks[2]);
					return tag;
				}
			}
		}*/
		/*if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityWatzCore reactor = (TileEntityWatzCore) core;
					tag.setBoolean("active", reactor.powerList > 0);
					tag.setLong(DataHelper.ENERGY, reactor.getSPower());
					tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
					tag.setDouble(DataHelper.OUTPUT, reactor.powerList);
					tag.setLong("heat", reactor.heatList);
					addTank("tank", tag, reactor.tank);
					return tag;
				}
			}
		}*/
		/*if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityFusionMultiblock reactor = (TileEntityFusionMultiblock) core;
					boolean active = reactor.isRunning();
					tag.setBoolean("active", active);
					tag.setLong(DataHelper.ENERGY, reactor.getSPower());
					tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
					tag.setDouble("consumption", active ? 20 : 0);
					tag.setDouble(DataHelper.OUTPUT, active ? reactor.isCoatingValid(te.getWorld()) ? 200000 : 100000 : 0);
					addTank("tank", tag, reactor.tanks[0]);
					addTank("tank2", tag, reactor.tanks[1]);
					addTank("tank3", tag, reactor.tanks[2]);
					return tag;
				}
			}
		}*/
		if (te instanceof TileEntityRBMKBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("heatD", ((TileEntityRBMKBase) te).heat);
			if (te instanceof TileEntityRBMKBoiler) {
				ArrayList values = HBMHooks.map.get(te);
				if (values != null) {
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble("outputmb", (int) values.get(1));
				}
				addTank("tank", tag, ((TileEntityRBMKBoiler) te).feed);
				addTank("tank2", tag, ((TileEntityRBMKBoiler) te).steam);
			}
			if (te instanceof TileEntityRBMKRod) {
				TileEntityRBMKRod rod = (TileEntityRBMKRod) te;
				/*ItemStack stack = rod.inventory.getStackInSlot(0);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemRBMKRod) {
					tag.setDouble("depletion", ((1.0D - ItemRBMKRod.getEnrichment(stack)) * 100000.0D) / 1000.0D);
					tag.setDouble("xenon", ItemRBMKRod.getPoison(stack));
					tag.setDouble("skin", ItemRBMKRod.getHullHeat(stack));
					tag.setDouble("c_heat", ItemRBMKRod.getCoreHeat(stack));
					tag.setDouble("melt", ((ItemRBMKRod) stack.getItem()).meltingPoint);
				}*/
			}
			return tag;
		}
		if (te instanceof TileEntityCoreTitanium) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreTitanium) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreTitanium) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityGeiger) {
			NBTTagCompound tag = new NBTTagCompound();
			/*RadiationSavedData data = RadiationSavedData.getData(te.getWorld());
			double rads = (int) (data.getRadNumFromCoord(te.getPos()) * 10.0F) / 10.0D;
			String chunkPrefix = ContaminationUtil.getPreffixFromRad(rads);
			tag.setString("chunkRad", chunkPrefix + rads + " RAD/s");*/
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
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_HBM,
				new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyeBlack",
					'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', com.hbm.items.ModItems.ingot_steel });

			Recipes.addKitRecipe(ItemCardType.KIT_HBM, ItemCardType.CARD_HBM);
	}
}
