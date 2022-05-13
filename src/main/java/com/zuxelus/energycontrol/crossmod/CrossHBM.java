package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.inventory.FluidTank;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.FusionRecipes;
import com.hbm.inventory.recipes.MachineRecipes;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemLens;
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

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSeleniumEngine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSeleniumEngine) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineShredder) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineShredder) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBoilerElectric) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineArcFurnace) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineArcFurnace) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCentrifuge) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCentrifuge) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasCent) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasCent) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineEPress) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineFrackingTower) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineFrackingTower) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineFrackingTower) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityDeuteriumExtractor) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityDeuteriumExtractor) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSchrabidiumTransmutator) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSchrabidiumTransmutator) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSchrabidiumTransmutator) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCMBFactory) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCMBFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCMBFactory) te).getMaxPower());
			addTank("tank", tag, ((TileEntityMachineCMBFactory) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineTeleporter) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTeleporter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTeleporter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityForceField) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityForceField) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityForceField) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineIGenerator) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineIGenerator) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityITER) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityITER) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityITER) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePlasmaHeater) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePlasmaHeater) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityChungus) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityChungus) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityChungus) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRadiolysis) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadiolysis) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadiolysis) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineCyclotron) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCyclotron) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCyclotron) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePumpjack) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineSolidifier) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSolidifier) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSolidifier) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineLiquefactor) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLiquefactor) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLiquefactor) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineGasFlare) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasFlare) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbofan) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) base).getMaxPower());
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
			if (base instanceof TileEntityMachineAssembler) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAssembler) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAssembler) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineMiningDrill) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiningDrill) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiningDrill) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityProxyEnergy) {
			TileEntity base = ((TileEntityProxyEnergy) te).getTE();
			if (base instanceof TileEntityFEL) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityFEL) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityFEL) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityProxyInventory) {
			TileEntity base = ((TileEntityProxyInventory) te).getTE();
			if (base instanceof TileEntityMachineCrystallizer) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityWatzCore) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityWatzCore) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityWatzCore) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityFWatzCore) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityFWatzCore) te).getPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityFWatzCore) te).getMaxPower());
			return tag;
		}
		/*if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorldObj().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionMultiblock) core).getSPower());
					tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionMultiblock) core).getMaxPower());
					return tag;
				}
			}
		}*/
		if (te instanceof TileEntityCoreEmitter) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreEmitter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreEmitter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityCoreStabilizer) {
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreStabilizer) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreStabilizer) te).getMaxPower());
			return tag;
		}
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
			return (int) Math.round(((TileEntityMachineReactorSmall) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);*/
		if (te instanceof TileEntityMachineReactorLarge)
			return (int) Math.round(((TileEntityMachineReactorLarge) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);
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
		if (te instanceof TileEntityMachineFrackingTower) {
			result.add(toFluidInfo(((TileEntityMachineFrackingTower) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineFrackingTower) te).tanks[1]));
			result.add(toFluidInfo(((TileEntityMachineFrackingTower) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			result.add(toFluidInfo(((TileEntityDeuteriumExtractor) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityDeuteriumExtractor) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityBarrel) {
			result.add(toFluidInfo(((TileEntityBarrel) te).tank));
			return result;
		}
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				result.add(toFluidInfo(((TileEntityMachineLargeTurbine) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineLargeTurbine) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntitySolarBoiler) {
				try {
					Field field = TileEntitySolarBoiler.class.getDeclaredField("water");
					field.setAccessible(true);
					result.add(toFluidInfo((FluidTank) field.get(te)));
					field = TileEntitySolarBoiler.class.getDeclaredField("steam");
					field.setAccessible(true);
					result.add(toFluidInfo((FluidTank) field.get(te)));
					return result;
				} catch (Throwable t) { }
			}
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
			if (base instanceof TileEntityMachineRadiolysis) {
				result.add(toFluidInfo(((TileEntityMachineRadiolysis) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineRadiolysis) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachineRadiolysis) base).tanks[2]));
				return result;
			}
			if (base instanceof TileEntityMachineCyclotron) {
				result.add(toFluidInfo(((TileEntityMachineCyclotron) base).coolant));
				result.add(toFluidInfo(((TileEntityMachineCyclotron) base).amat));
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
			if (base instanceof TileEntityMachineSolidifier) {
				result.add(toFluidInfo(((TileEntityMachineSolidifier) base).tank));
				return result;
			}
			if (base instanceof TileEntityMachineLiquefactor) {
				result.add(toFluidInfo(((TileEntityMachineLiquefactor) base).tank));
				return result;
			}
			if (base instanceof TileEntityTowerSmall) {
				result.add(toFluidInfo(((TileEntityTowerSmall) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityTowerSmall) base).tanks[1]));
				return result;
			}
			if (base instanceof TileEntityMachineCatalyticCracker) {
				result.add(toFluidInfo(((TileEntityMachineCatalyticCracker) base).tanks[0]));
				result.add(toFluidInfo(((TileEntityMachineCatalyticCracker) base).tanks[1]));
				result.add(toFluidInfo(((TileEntityMachineCatalyticCracker) base).tanks[2]));
				result.add(toFluidInfo(((TileEntityMachineCatalyticCracker) base).tanks[3]));
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
		}*/
		if (te instanceof TileEntityMachineReactorLarge) {
			result.add(toFluidInfo(((TileEntityMachineReactorLarge) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityMachineReactorLarge) te).tanks[1]));
			result.add(toFluidInfo(((TileEntityMachineReactorLarge) te).tanks[2]));
			return result;
		}
		if (te instanceof TileEntityWatzCore) {
			result.add(toFluidInfo(((TileEntityWatzCore) te).tank));
			return result;
		}
		if (te instanceof TileEntityFWatzCore) {
			result.add(toFluidInfo(((TileEntityFWatzCore) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityFWatzCore) te).tanks[1]));
			result.add(toFluidInfo(((TileEntityFWatzCore) te).tanks[2]));
			return result;
		}
		/*if (te instanceof TileEntityFusionHatch) {
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
		if (te instanceof TileEntityCore) {
			result.add(toFluidInfo(((TileEntityCore) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityCore) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityCoreEmitter) {
			result.add(toFluidInfo(((TileEntityCoreEmitter) te).tank));
			return result;
		}
		if (te instanceof TileEntityCoreInjector) {
			result.add(toFluidInfo(((TileEntityCoreInjector) te).tanks[0]));
			result.add(toFluidInfo(((TileEntityCoreInjector) te).tanks[1]));
			return result;
		}
		if (te instanceof TileEntityCoreReceiver) {
			result.add(toFluidInfo(((TileEntityCoreEmitter) te).tank));
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
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).getMaxPower());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("active", ((TileEntityMachineCoal) te).burnTime > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).getMaxPower());
			if (((TileEntityMachineCoal) te).burnTime > 0) {
				tag.setDouble(DataHelper.CONSUMPTION, 1);
				tag.setDouble(DataHelper.OUTPUT, 25);
			} else {
				tag.setDouble(DataHelper.CONSUMPTION, 0);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
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
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).getMaxPower());
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
			tag.setLong(DataHelper.CAPACITY, diesel.getMaxPower());
			tag.setInteger("pistons", diesel.pistonCount);
			addTank("tank", tag, diesel.tank);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			NBTTagCompound tag = new NBTTagCompound();

			if (((TileEntityMachineRTG) te).heat > 0) {
				tag.setBoolean("active", true);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineRTG) te).heat * 5);
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
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 700);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = calcAmgenOutput(te);
			tag.setBoolean("active", output > 0 && ((TileEntityMachineAmgen) te).getPower() < ((TileEntityMachineAmgen) te).getMaxPower());
			tag.setDouble(DataHelper.OUTPUT, output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = Math.max(((TileEntityMachineSPP) te).checkStructure() * 15, 0);
			tag.setBoolean("active", output > 0);
			tag.setDouble(DataHelper.OUTPUT, output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine turbine = (TileEntityMachineTurbine) te;
			NBTTagCompound tag = new NBTTagCompound();
			ArrayList values = getHookValues(turbine);
			if (values != null) {
				tag.setBoolean("active", (int) values.get(1) > 0);
				tag.setDouble("consumption", (int) values.get(0));
				tag.setDouble("outputmb", (int) values.get(1));
				tag.setDouble(DataHelper.OUTPUT, (int) values.get(2));
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).getMaxPower());
			addTank("tank", tag, ((TileEntityMachineTurbine) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineTurbine) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineShredder) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineShredder) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineShredder) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineBoiler) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineBoiler boiler = (TileEntityMachineBoiler) te;
			tag.setLong("hull", boiler.heat / 100);
			
			int consumption = 0;
			int output = 0;
			Object[] outs = MachineRecipes.getBoilerOutput(boiler.tanks[0].getTankType());
			if (outs != null)
				for (int j = 0; j < boiler.heat / ((Integer) outs[3]).intValue(); j++)
					if (boiler.tanks[0].getFill() >= ((Integer) outs[2]).intValue() &&
						boiler.tanks[1].getFill() + ((Integer) outs[1]).intValue() <= boiler.tanks[1].getMaxFill()) {
						consumption += ((Integer) outs[2]).intValue();
						output += ((Integer) outs[1]).intValue();
					}
			tag.setDouble("consumption", consumption);
			tag.setDouble("outputmb", output);
			addTank("tank", tag, ((TileEntityMachineBoiler) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBoilerElectric) te).getMaxPower());
			tag.setLong("hull", ((TileEntityMachineBoilerElectric) te).heat / 100);
			addTank("tank", tag, ((TileEntityMachineBoilerElectric) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineBoilerElectric) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineArcFurnace) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineArcFurnace) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineArcFurnace) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCentrifuge) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCentrifuge) te).progress > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCentrifuge) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCentrifuge) te).getMaxPower());
			tag.setInteger("progress", ((TileEntityMachineCentrifuge) te).progress);
			return tag;
		}
		if (te instanceof TileEntityMachineGasCent) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineGasCent) te).progress > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasCent) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasCent) te).getMaxPower());
			tag.setInteger("progress", ((TileEntityMachineGasCent) te).progress);
			addTank("tank", tag, ((TileEntityMachineGasCent) te).tank);
			addTank("tank2", tag, ((TileEntityMachineGasCent) te).inputTank);
			addTank("tank3", tag, ((TileEntityMachineGasCent) te).outputTank);
			return tag;
		}
		if (te instanceof TileEntityMachineEPress) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineEPress) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineEPress) te).getMaxPower());
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
		if (te instanceof TileEntityReactorResearch) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("heat", Math.round(((TileEntityReactorResearch) te).heat * 2.0E-5D * 980.0D + 20.0D));
			tag.setInteger("flux", ((TileEntityReactorResearch) te).totalFlux);
			tag.setInteger("water", ((TileEntityReactorResearch) te).water);
			return tag;
		}
		if (te instanceof TileEntityMachineReactorBreeding) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setInteger("flux", ((TileEntityMachineReactorBreeding) te).flux);
			return tag;
		}
		if (te instanceof TileEntityMachineFrackingTower) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineFrackingTower) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineFrackingTower) te).getMaxPower());
			addTank("tank", tag, ((TileEntityMachineFrackingTower) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityMachineFrackingTower) te).tanks[1]);
			addTank("tank3", tag, ((TileEntityMachineFrackingTower) te).tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityDeuteriumExtractor) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityDeuteriumExtractor) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityDeuteriumExtractor) te).getMaxPower());
			addTank("tank", tag, ((TileEntityDeuteriumExtractor) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityDeuteriumExtractor) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineSchrabidiumTransmutator) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSchrabidiumTransmutator) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSchrabidiumTransmutator) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityMachineCMBFactory) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCMBFactory) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCMBFactory) te).getMaxPower());
			addTank("tank", tag, ((TileEntityMachineCMBFactory) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineTeleporter) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTeleporter) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTeleporter) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityForceField) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityForceField) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityForceField) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityBarrel) {
			NBTTagCompound tag = new NBTTagCompound();
			addTank("tank", tag, ((TileEntityBarrel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				ArrayList values = getHookValues(base);
				if (values != null) {
					tag.setBoolean("active", (int) values.get(1) > 0);
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(1));
					tag.setDouble("outputmb", (int) values.get(2));
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).getMaxPower());
				addTank("tank", tag, ((TileEntityMachineLargeTurbine) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineLargeTurbine) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntitySolarBoiler) {
				try {
					Field field = TileEntitySolarBoiler.class.getDeclaredField("water");
					field.setAccessible(true);
					addTank("tank", tag, (FluidTank) field.get(te));
					field = TileEntitySolarBoiler.class.getDeclaredField("steam");
					field.setAccessible(true);
					addTank("tank2", tag, (FluidTank) field.get(te));
					return tag;
				} catch (Throwable t) { }
			}
			if (base instanceof TileEntityMachineIGenerator) {
				TileEntityMachineIGenerator generator = (TileEntityMachineIGenerator) base;
				ArrayList values = getHookValues(base);
				if (values != null) {
					int output = (int) values.get(0);
					tag.setBoolean("active", output > 0);
					tag.setDouble(DataHelper.OUTPUT, output);
				}
				tag.setLong(DataHelper.ENERGY, generator.getPower());
				tag.setLong(DataHelper.CAPACITY, generator.getMaxPower());
				addTank("tank", tag, generator.tanks[0]);
				addTank("tank2", tag, generator.tanks[1]);
				addTank("tank3", tag, generator.tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				TileEntityITER reactor = (TileEntityITER) base;
				Boolean active = reactor.isOn && reactor.plasma.getFill() > 0;
				tag.setBoolean("active", active);
				tag.setLong(DataHelper.ENERGY, reactor.getPower());
				tag.setLong(DataHelper.CAPACITY, reactor.getMaxPower());
				if (active) {
					int output = FusionRecipes.getSteamProduction(reactor.plasma.getTankType());
					tag.setDouble("consumption", output * 10);
					tag.setDouble("outputmb", output);
				} else {
					tag.setDouble("consumption", 0);
					tag.setDouble("outputmb", 0);
				}
				addTank("tank", tag, reactor.tanks[0]);
				addTank("tank2", tag, reactor.tanks[1]);
				addTank("tank3", tag, reactor.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				TileEntityMachinePlasmaHeater heater = (TileEntityMachinePlasmaHeater) base;
				tag.setLong(DataHelper.ENERGY, heater.getPower());
				tag.setLong(DataHelper.CAPACITY, heater.getMaxPower());
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
				ArrayList values = getHookValues(base);
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
				ArrayList values = getHookValues(engine);
				if (values != null) {
					tag.setBoolean("active", (int) values.get(1) > 0);
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble("outputmb", (int) values.get(1));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(2));
				}
				tag.setLong(DataHelper.ENERGY, engine.getPower());
				tag.setLong(DataHelper.CAPACITY, engine.getMaxPower());
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
			if (base instanceof TileEntityReactorZirnox) {
				TileEntityReactorZirnox reactor = ((TileEntityReactorZirnox) base);
				tag.setDouble(DataHelper.HEAT, Math.round(reactor.heat * 1.0E-5D * 780.0D + 20.0D));
				tag.setDouble(DataHelper.MAXHEAT, Math.round(reactor.maxHeat * 1.0E-5D * 780.0D + 20.0D));
				tag.setLong(DataHelper.PRESSURE, Math.round(reactor.pressure * 1.0E-5D * 30.0D));
				int output = 0;
				if (reactor.heat > 10256 && reactor.heat < 100000 && reactor.water.getFill() > 0
						&& reactor.carbonDioxide.getFill() > 0 && reactor.steam.getFill() < reactor.steam.getMaxFill())
					output = (int) ((reactor.heat - 10256.0F) / 100000.0F
							* Math.min(reactor.carbonDioxide.getFill() / 14000.0F, 1.0F) * 25.0F * 5.0F);
				tag.setDouble("consumption", output);
				tag.setDouble("outputmb", output);
				addTank("tank", tag, ((TileEntityReactorZirnox) base).steam);
				addTank("tank2", tag, ((TileEntityReactorZirnox) base).carbonDioxide);
				addTank("tank3", tag, ((TileEntityReactorZirnox) base).water);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setBoolean("active", ((TileEntityMachineRadGen) base).isOn);
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).getMaxPower());
				int output = 0;
				for (int i = 0; i < 12; i++)
					if (((TileEntityMachineRadGen) base).processing[i] != null)
						output += ((TileEntityMachineRadGen) base).production[i];
				tag.setDouble("output", output);
				return tag;
			}
			if (base instanceof TileEntityMachineRadiolysis) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadiolysis) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadiolysis) base).getMaxPower());
				tag.setDouble("output", ((TileEntityMachineRadiolysis) base).heat * 10);
				addTank("tank", tag, ((TileEntityMachineRadiolysis) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineRadiolysis) base).tanks[1]);
				addTank("tank3", tag, ((TileEntityMachineRadiolysis) base).tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityMachineCyclotron) {
				TileEntityMachineCyclotron cyclotron = (TileEntityMachineCyclotron) base;
				tag.setBoolean("active", cyclotron.isOn && cyclotron.progress > 0);
				tag.setDouble(DataHelper.ENERGY, cyclotron.getPower());
				tag.setDouble(DataHelper.CAPACITY, cyclotron.getMaxPower());
				tag.setDouble("consumptionHE", cyclotron.progress > 0 ? cyclotron.consumption - 100000 * cyclotron.getConsumption() : 0);
				addTank("tank", tag, cyclotron.coolant);
				addTank("tank2", tag, cyclotron.amat);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).getMaxPower());
				addTank("tank", tag, ((TileEntityMachineOilWell) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineOilWell) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachinePumpjack) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).getMaxPower());
				addTank("tank", tag, ((TileEntityMachinePumpjack) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachinePumpjack) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineSolidifier) {
				TileEntityMachineSolidifier solidifier = (TileEntityMachineSolidifier) base;
				tag.setBoolean(DataHelper.ACTIVE, solidifier.progress > 0);
				tag.setLong(DataHelper.ENERGY, solidifier.getPower());
				tag.setLong(DataHelper.CAPACITY, solidifier.getMaxPower());
				tag.setDouble("consumptionHE", solidifier.progress > 0 ? solidifier.usage : 0);
				addTank("tank", tag, solidifier.tank);
				return tag;
			}
			if (base instanceof TileEntityMachineLiquefactor) {
				TileEntityMachineLiquefactor liquefactor = (TileEntityMachineLiquefactor) base;
				tag.setBoolean(DataHelper.ACTIVE, liquefactor.progress > 0);
				tag.setLong(DataHelper.ENERGY, liquefactor.getPower());
				tag.setLong(DataHelper.CAPACITY, liquefactor.getMaxPower());
				tag.setDouble("consumptionHE", liquefactor.progress > 0 ? liquefactor.usage : 0);
				addTank("tank", tag, liquefactor.tank);
				return tag;
			}
			if (base instanceof TileEntityTowerSmall) {
				addTank("tank", tag, ((TileEntityTowerSmall) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityTowerSmall) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineCatalyticCracker) {
				addTank("tank", tag, ((TileEntityMachineCatalyticCracker) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineCatalyticCracker) base).tanks[1]);
				addTank("tank3", tag, ((TileEntityMachineCatalyticCracker) base).tanks[2]);
				addTank("tank4", tag, ((TileEntityMachineCatalyticCracker) base).tanks[3]);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorldObj().getTileEntity(((TileEntityDummy) te).targetX, ((TileEntityDummy) te).targetY, ((TileEntityDummy) te).targetZ);
			if (base instanceof TileEntityMachineGasFlare) {
				NBTTagCompound tag = new NBTTagCompound();
				if (((TileEntityMachineGasFlare) base).tank.getFill() >= 10) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", 10);
					tag.setDouble(DataHelper.OUTPUT, 50);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasFlare) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) base).getMaxPower());
				addTank("tank", tag, ((TileEntityMachineGasFlare) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				TileEntityMachineTurbofan fan = (TileEntityMachineTurbofan) base;
				NBTTagCompound tag = new NBTTagCompound();
				int nrg = 1250;
				int cnsp = 1;
				ItemStack stack = ((IInventory) fan).getStackInSlot(2);
				if (stack != null) {
					if (stack.getItem() == com.hbm.items.ModItems.upgrade_afterburn_1) {
						nrg *= 2;
						cnsp = (int) (cnsp * 2.5D);
					}
					if (stack.getItem() == com.hbm.items.ModItems.upgrade_afterburn_2) {
						nrg *= 3;
						cnsp *= 5;
					}
					if (stack.getItem() == com.hbm.items.ModItems.upgrade_afterburn_3) {
						nrg *= 4;
						cnsp = (int) (cnsp * 7.5D);
					}
				}
				if (fan.tank.getFill() >= cnsp) {
					tag.setBoolean("active", true);
					tag.setDouble("consumption", cnsp);
					tag.setDouble(DataHelper.OUTPUT, nrg);
				} else {
					tag.setBoolean("active", false);
					tag.setDouble("consumption", 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}
				tag.setLong(DataHelper.ENERGY, fan.getPower());
				tag.setLong(DataHelper.CAPACITY, fan.getMaxPower());
				addTank("tank", tag, fan.tank);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityAMSLimiter) base).power);
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSLimiter) base).maxPower);
				tag.setLong("heatL", ((TileEntityAMSLimiter) base).heat);
				addTank("tank", tag, ((TileEntityAMSLimiter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				NBTTagCompound tag = new NBTTagCompound();
				TileEntityAMSBase reactor = (TileEntityAMSBase) base;
				/*double output = calcASMBaseOutput(reactor);
				tag.setBoolean("active", output > 0);
				tag.setDouble(DataHelper.OUTPUT, output);*/
				tag.setLong(DataHelper.ENERGY, reactor.power);
				tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
				tag.setLong("heatL", reactor.heat);
				addTank("tank", tag, reactor.tanks[0]);
				addTank("tank2", tag, reactor.tanks[1]);
				addTank("tank3", tag, reactor.tanks[2]);
				addTank("tank4", tag, reactor.tanks[3]);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityAMSEmitter) base).power);
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSEmitter) base).maxPower);
				tag.setLong("heatL", ((TileEntityAMSEmitter) base).heat);
				addTank("tank", tag, ((TileEntityAMSEmitter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAssembler) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAssembler) base).getMaxPower());
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).getMaxPower());
				addTank("tank", tag, ((TileEntityMachineRefinery) base).tanks[0]);
				addTank("tank2", tag, ((TileEntityMachineRefinery) base).tanks[1]);
				addTank("tank3", tag, ((TileEntityMachineRefinery) base).tanks[2]);
				addTank("tank4", tag, ((TileEntityMachineRefinery) base).tanks[3]);
				addTank("tank5", tag, ((TileEntityMachineRefinery) base).tanks[4]);
				return tag;
			}
			if (base instanceof TileEntityMachineMiningDrill) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiningDrill) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiningDrill) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityProxyEnergy) {
			TileEntity base = ((TileEntityProxyEnergy) te).getTE();
			if (base instanceof TileEntityFEL) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityFEL) base).isOn);
				tag.setLong(DataHelper.ENERGY, ((TileEntityFEL) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityFEL) base).getMaxPower());
				return tag;
			}
		}
		if (te instanceof TileEntityProxyInventory) {
			TileEntity base = ((TileEntityProxyInventory) te).getTE();
			if (base instanceof TileEntityMachineCrystallizer) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).getMaxPower());
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
		if (te instanceof TileEntityMachineReactorLarge) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineReactorLarge reactor = (TileEntityMachineReactorLarge) te;
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
			FluidType type = reactor.tanks[2].getTankType();
			if (type == Fluids.STEAM)
				water /= 100.0D;
			if (type == Fluids.HOTSTEAM)
				water /= 10.0D;
			tag.setDouble("consumption", (int) Math.ceil(water));
			tag.setDouble("outputmb", (int) Math.floor(steam));
			addTank("tank", tag, reactor.tanks[0]);
			addTank("tank2", tag, reactor.tanks[1]);
			addTank("tank3", tag, reactor.tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityWatzCore) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityWatzCore reactor = (TileEntityWatzCore) te;
			tag.setBoolean("active", reactor.powerList > 0);
			tag.setLong(DataHelper.ENERGY, reactor.getPower());
			tag.setLong(DataHelper.CAPACITY, reactor.getMaxPower());
			tag.setDouble(DataHelper.OUTPUT, reactor.powerList);
			tag.setLong("heatL", reactor.heatList);
			addTank("tank", tag, reactor.tank);
			return tag;
		}
		if (te instanceof TileEntityFWatzCore) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityFWatzCore core = (TileEntityFWatzCore) te;
			Boolean active = core.hasFuse() && core.getSingularityType() > 0;
			int j = core.getSingularityType();
			tag.setBoolean("active", active);
			if (active) {
				if (core.cooldown) {
					tag.setString("consumption_", "0");
					tag.setDouble(DataHelper.OUTPUTMB, j == 1 ? 1500 : j == 2 ? 3000 : j == 3 ? 750 : j == 4 ? 7500 : j == 5 ? 15000 : 0);
					tag.setDouble("output", 0);
				} else {
					tag.setDouble(DataHelper.OUTPUTMB, 0);

					if (j == 1 && core.tanks[1].getFill() - 75 >= 0 && core.tanks[2].getFill() - 75 >= 0) {
						tag.setString("consumption_", "150/75/75");
						tag.setDouble("output", 5000000L);
					}
					if (j == 2 && core.tanks[1].getFill() - 75 >= 0 && core.tanks[2].getFill() - 35 >= 0) {
						tag.setString("consumption_", "75/35/30");
						tag.setDouble("output", 2500000L);
					}
					if (j == 3 && core.tanks[1].getFill() - 75 >= 0 && core.tanks[2].getFill() - 140 >= 0) {
						tag.setString("consumption_", "300/75/140");
						tag.setDouble("output", 10000000L);
					}
					if (j == 4 && core.tanks[1].getFill() - 100 >= 0 && core.tanks[2].getFill() - 100 >= 0) {
						tag.setString("consumption_", "100/100/100");
						tag.setDouble("output", 10000000L);
					}
					if (j == 5 && core.tanks[1].getFill() - 15 >= 0 && core.tanks[2].getFill() - 15 >= 0) {
						tag.setString("consumption_", "150/15/15");
						tag.setDouble("output", 100000000L);
					}
				}
			} else {
				tag.setString("consumption_", "0");
				tag.setDouble(DataHelper.OUTPUTMB, 0);
				tag.setDouble("output", 0);
			}
			tag.setLong(DataHelper.ENERGY, core.getPower());
			tag.setLong(DataHelper.CAPACITY, core.getMaxPower());
			addTank("tank", tag, core.tanks[0]);
			addTank("tank2", tag, core.tanks[1]);
			addTank("tank3", tag, core.tanks[2]);
			return tag;
		}
		/*if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityFusionMultiblock reactor = (TileEntityFusionMultiblock) core;
					boolean active = reactor.isRunning();
					tag.setBoolean("active", active);
					tag.setLong(DataHelper.ENERGY, reactor.getSPower());
					tag.setLong(DataHelper.CAPACITY, reactor.getMaxPower());
					tag.setDouble("consumption", active ? 20 : 0);
					tag.setDouble(DataHelper.OUTPUT, active ? reactor.isCoatingValid(te.getWorld()) ? 200000 : 100000 : 0);
					addTank("tank", tag, reactor.tanks[0]);
					addTank("tank2", tag, reactor.tanks[1]);
					addTank("tank3", tag, reactor.tanks[2]);
					return tag;
				}
			}
		}*/
		if (te instanceof TileEntityCore) {
			NBTTagCompound tag = new NBTTagCompound();
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
			addTank("tank", tag, ((TileEntityCore) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityCore) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCoreEmitter) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityCoreEmitter emitter = (TileEntityCoreEmitter) te;
			tag.setLong(DataHelper.ENERGY, emitter.getPower());
			tag.setLong(DataHelper.CAPACITY, emitter.getMaxPower());
			tag.setDouble("consumption", emitter.joules > 0 || emitter.prev > 0 ? 20 : 0);
			tag.setDouble("consumptionHE", emitter.maxPower * emitter.watts / 2000);
			addTank("tank", tag, emitter.tank);
			return tag;
		}
		if (te instanceof TileEntityCoreStabilizer) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityCoreStabilizer stabilizer = (TileEntityCoreStabilizer) te;
			tag.setLong(DataHelper.ENERGY, stabilizer.getPower());
			tag.setLong(DataHelper.CAPACITY, stabilizer.getMaxPower());
			int demand = (int) Math.pow(stabilizer.watts, 4);
			long damage = ItemLens.getLensDamage(stabilizer.slots[0]);
			ItemLens lens = (ItemLens) com.hbm.items.ModItems.ams_lens;
			if (stabilizer.getPower() >= demand && stabilizer.slots[0] != null && stabilizer.slots[0].getItem() == lens && damage < 432000000L)
				tag.setDouble("consumptionHE", demand);
			else
				tag.setDouble("consumptionHE", 0);
			tag.setLong("durability", 432000000L - damage);
			return tag;
		}
		if (te instanceof TileEntityCoreInjector) {
			NBTTagCompound tag = new NBTTagCompound();
			addTank("tank", tag, ((TileEntityCoreInjector) te).tanks[0]);
			addTank("tank2", tag, ((TileEntityCoreInjector) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityCoreReceiver) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("consumption", ((TileEntityCoreReceiver) te).joules > 0 ? 20 : 0);
			tag.setDouble("output", ((TileEntityCoreReceiver) te).joules * 5000);
			addTank("tank", tag, ((TileEntityCoreReceiver) te).tank);
			return tag;
		}
		if (te instanceof TileEntityRBMKBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("heat", ((TileEntityRBMKBase) te).heat);
			if (te instanceof TileEntityRBMKBoiler) {
				ArrayList values = getHookValues(te);
				if (values != null) {
					tag.setDouble("consumption", (int) values.get(0));
					tag.setDouble("outputmb", (int) values.get(1));
				}
				addTank("tank", tag, ((TileEntityRBMKBoiler) te).feed);
				addTank("tank2", tag, ((TileEntityRBMKBoiler) te).steam);
			}
			if (te instanceof TileEntityRBMKRod) {
				TileEntityRBMKRod rod = (TileEntityRBMKRod) te;
				ItemStack stack = rod.slots[0];
				if (stack != null && stack.getItem() instanceof ItemRBMKRod) {
					tag.setDouble("depletion", ((1.0D - ItemRBMKRod.getEnrichment(stack)) * 100000.0D) / 1000.0D);
					tag.setDouble("xenon", ItemRBMKRod.getPoison(stack));
					tag.setDouble("skin", ItemRBMKRod.getHullHeat(stack));
					tag.setDouble("c_heat", ItemRBMKRod.getCoreHeat(stack));
					tag.setDouble("melt", ((ItemRBMKRod) stack.getItem()).meltingPoint);
				}
			}
			return tag;
		}
		if (te instanceof TileEntityCoreTitanium) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityCoreTitanium) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreTitanium) te).getMaxPower());
			return tag;
		}
		if (te instanceof TileEntityGeiger) {
			NBTTagCompound tag = new NBTTagCompound();
			double rads = ((int) Math.ceil(ChunkRadiationManager.proxy.getRadiation(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord)) * 10.0F) / 10.0D;
			String chunkPrefix = ContaminationUtil.getPreffixFromRad(rads);
			tag.setString("chunkRad", chunkPrefix + rads + " RAD/s");
			return tag;
		}
		return null;
	}

	private double calcAmgenOutput(TileEntity te) {
		Block block = te.getWorldObj().getBlock(te.xCoord, te.yCoord, te.zCoord);
		long power = 0;
		if (block == ModBlocks.machine_amgen)
			power += (long) ChunkRadiationManager.proxy.getRadiation(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		else if (block == ModBlocks.machine_geo) {
			power += checkGeoInteraction(te.getWorldObj().getBlock(te.xCoord, te.yCoord + 1, te.zCoord));
			power += checkGeoInteraction(te.getWorldObj().getBlock(te.xCoord, te.yCoord - 1, te.zCoord));
		}
		return power;
	}

	private long checkGeoInteraction(Block block) {
		if (block == ModBlocks.geysir_water)
			return 75L;
		if (block == ModBlocks.geysir_chlorine)
			return 100L;
		if (block == ModBlocks.geysir_vapor)
			return 50L;
		if (block == ModBlocks.geysir_nether)
			return 500L;
		if (block == Blocks.lava)
			return 100L;
		if (block == Blocks.flowing_lava)
			return 25L;
		return 0L;
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
}
