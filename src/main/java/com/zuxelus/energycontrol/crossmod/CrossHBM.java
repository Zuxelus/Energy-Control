package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.capability.HbmLivingProps;
import com.hbm.config.WorldConfig;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IItemFluidHandler;
import com.hbm.inventory.FusionRecipes;
import com.hbm.inventory.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.machine.ItemCatalyst;
import com.hbm.items.machine.ItemRBMKRod;
import com.hbm.items.special.ItemAMSCore;
import com.hbm.saveddata.RadiationSavedData;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.*;
import com.hbm.util.ContaminationUtil;
import com.hbm.world.generator.DungeonToolbox;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
import com.zuxelus.energycontrol.hooks.HBMHooks;
import com.zuxelus.energycontrol.items.cards.ItemCardHBM;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitHBM;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CrossHBM extends CrossModBase {

	@Override
	public boolean isElectricItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemBattery)
			return true;
		return false;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed, int tier) {
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
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineDiesel) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSeleniumEngine) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSeleniumEngine) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getSPower());
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getSPower());
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
		if (te instanceof TileEntityProxyCombo) {
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineIGenerator) base).getSPower());
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
			if (base instanceof TileEntityMachineCrystallizer) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityChungus) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityChungus) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityChungus) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorld().getTileEntity(((TileEntityDummy) te).target);
			if (base instanceof TileEntityMachineChemplant) {
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemplant) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemplant) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineGasFlare) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineGasFlare) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineTurbofan) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineTurbofan) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSLimiter) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSLimiter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSBase) base).getSPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSBase) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityAMSEmitter) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityAMSEmitter) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineAssembler) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineAssembler) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineAssembler) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachinePumpjack) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachinePumpjack) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).maxPower);
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).maxPower);
				return tag;
			}
		}
		if (te instanceof TileEntityWatzHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 3));
				if (core instanceof TileEntityWatzCore) {
					tag.setDouble(DataHelper.ENERGY, ((TileEntityWatzCore) core).getSPower());
					tag.setDouble(DataHelper.CAPACITY, ((TileEntityWatzCore) core).maxPower);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityFusionHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 8));
				if (core instanceof TileEntityFusionMultiblock) {
					tag.setDouble(DataHelper.ENERGY, ((TileEntityFusionMultiblock) core).getSPower());
					tag.setDouble(DataHelper.CAPACITY, ((TileEntityFusionMultiblock) core).maxPower);
					return tag;
				}
			}
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
		if (te instanceof TileEntityMachineReactorSmall)
			return (int) Math.round(((TileEntityMachineReactorSmall) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);
		if (te instanceof TileEntityMachineReactorLarge)
			return (int) Math.round(((TileEntityMachineReactorLarge) te).hullHeat * 1.0E-5D * 980.0D + 20.0D);
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
		if (te instanceof TileEntityBarrel) {
			result.add(new FluidInfo(((TileEntityBarrel) te).tank));
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
			if (base instanceof TileEntityMachineChemplant) {
				result.add(new FluidInfo(((TileEntityMachineChemplant) base).tanks[0]));
				result.add(new FluidInfo(((TileEntityMachineChemplant) base).tanks[1]));
				result.add(new FluidInfo(((TileEntityMachineChemplant) base).tanks[2]));
				result.add(new FluidInfo(((TileEntityMachineChemplant) base).tanks[3]));
				return result;
			}
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
	public NBTTagCompound getCardData(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() != null && state.getBlock().getClass().getName().equals("com.hbm.blocks.machine.FactoryHatch"))
				for (EnumFacing dir : EnumFacing.HORIZONTALS) {
					te = world.getTileEntity(pos.offset(dir));
					if (te != null)
						break;
				}
		}
		if (te instanceof TileEntityMachineBattery) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBattery) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBattery) te).maxPower);
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong(DataHelper.DIFF, ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof TileEntityMachineCoal) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCoal) te).burnTime > 0);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCoal) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCoal) te).maxPower);
			if (((TileEntityMachineCoal) te).burnTime > 0)
				tag.setDouble(DataHelper.OUTPUT, 25);
			else
				tag.setDouble(DataHelper.OUTPUT, 0);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCoal) te).tank);
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
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineDiesel) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineDiesel) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineDiesel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityMachineSeleniumEngine) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntityMachineSeleniumEngine diesel = ((TileEntityMachineSeleniumEngine) te);
			if (diesel.hasAcceptableFuel() && diesel.tank.getFluidAmount() > 0 && diesel.pistonCount > 2) {
				tag.setBoolean(DataHelper.ACTIVE, true);
				tag.setDouble(DataHelper.OUTPUT, diesel.getHEFromFuel() * Math.pow(diesel.pistonCount, 1.15D));
			} else {
				tag.setBoolean(DataHelper.ACTIVE, false);
				tag.setDouble(DataHelper.OUTPUT, 0);
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSeleniumEngine) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSeleniumEngine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineSeleniumEngine) te).tank);
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
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRTG) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRTG) te).powerMax);
			return tag;
		}
		if (te instanceof TileEntityMachineMiniRTG) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, true);
			tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 2500 : 70);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineMiniRTG) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineMiniRTG) te).getBlockType() == ModBlocks.machine_powerrtg ? 50000 : 1400);
			return tag;
		}
		if (te instanceof TileEntityMachineAmgen) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = calcAmgenOutput(te.getWorld(), te.getBlockType(), te.getPos());
			tag.setBoolean(DataHelper.ACTIVE, output > 0);
			tag.setDouble(DataHelper.OUTPUT, output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineAmgen) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineAmgen) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineSPP) {
			NBTTagCompound tag = new NBTTagCompound();
			double output = Math.max(((TileEntityMachineSPP) te).checkStructure() * 15, 0);
			tag.setBoolean(DataHelper.ACTIVE, output > 0);
			tag.setDouble(DataHelper.OUTPUT, output);
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineSPP) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineSPP) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityMachineTurbine) {
			TileEntityMachineTurbine turbine = (TileEntityMachineTurbine) te;
			NBTTagCompound tag = new NBTTagCompound();
			ArrayList values = getHookValues(turbine);
			if (values != null) {
				Object[] outs = MachineRecipes.getTurbineOutput((turbine.tanks[0].getFluid() == null) ? null : turbine.tanks[0].getFluid().getFluid());
				if (outs != null) {
					double cycles = 0;
					for (int i = 0; i < 20; i++)
						cycles += (int) values.get(i);
					cycles /= 20;
					tag.setBoolean(DataHelper.ACTIVE, cycles > 0);
					tag.setDouble(DataHelper.CONSUMPTION, ((Integer) outs[2]).intValue() * cycles);
					tag.setDouble(DataHelper.OUTPUT, ((Integer) outs[3]).intValue() * cycles);
					tag.setDouble(DataHelper.OUTPUTMB, ((Integer) outs[1]).intValue() * cycles);
				} else {
					tag.setBoolean(DataHelper.ACTIVE, false);
					tag.setDouble(DataHelper.CONSUMPTION, 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
					tag.setDouble(DataHelper.OUTPUTMB, 0);
				}
			}
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbine) te).getSPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbine) te).maxPower);
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineTurbine) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineTurbine) te).tanks[1]);
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
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineBoiler) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineBoiler) te).tanks[1]);
			return tag;
		}
		if (te instanceof TileEntityMachineBoilerElectric) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong(DataHelper.ENERGY, ((TileEntityMachineBoilerElectric) te).getPower());
			tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineBoilerElectric) te).maxPower);
			tag.setLong("hull", ((TileEntityMachineBoilerElectric) te).heat / 100);
			ArrayList values = getHookValues(te);
			if (values != null) {
				tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
				tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(1));
			}
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineBoilerElectric) te).tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineBoilerElectric) te).tanks[1]);
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
			FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityBarrel) te).tank);
			return tag;
		}
		if (te instanceof TileEntityProxyCombo) {
			NBTTagCompound tag = new NBTTagCompound();
			TileEntity base = ((TileEntityProxyCombo) te).getTile();
			if (base instanceof TileEntityMachineLargeTurbine) {
				ArrayList values = getHookValues(base);
				if (values != null) {
					tag.setBoolean(DataHelper.ACTIVE, (int) values.get(1) > 0);
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(2));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(1));
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineLargeTurbine) base).getSPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineLargeTurbine) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineLargeTurbine) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineLargeTurbine) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntitySolarBoiler) {
				IFluidTankProperties tanks[] = ((TileEntitySolarBoiler) base).getTankProperties();
				ArrayList values = getHookValues(base);
				if (values != null) {
					tag.setDouble("heat", (int) values.get(0));
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(1));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(1) * 100);
				}
				FluidInfo.addTank(DataHelper.TANK, tag, tanks[0].getContents());
				FluidInfo.addTank(DataHelper.TANK2, tag, tanks[1].getContents());
				return tag;
			}
			if (base instanceof TileEntityMachineIGenerator) {
				TileEntityMachineIGenerator generator = (TileEntityMachineIGenerator) base;
				double output = calcIGeneratorOutput(generator);
				tag.setBoolean(DataHelper.ACTIVE, output > 0);
				tag.setDouble(DataHelper.OUTPUT, output);
				tag.setLong(DataHelper.ENERGY, generator.getSPower());
				tag.setLong(DataHelper.CAPACITY, generator.maxPower);
				tag.setInteger("temp", generator.temperature + 300);
				tag.setInteger("speed", generator.torque);
				FluidInfo.addTank(DataHelper.TANK, tag, generator.tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, generator.tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, generator.tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityITER) {
				TileEntityITER reactor = (TileEntityITER) base;
				tag.setLong(DataHelper.ENERGY, reactor.getPower());
				tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
				int output = FusionRecipes.getSteamProduction(reactor.plasmaType) * 20;
				tag.setDouble(DataHelper.CONSUMPTION, reactor.isOn ? output * 10 : 0);
				tag.setDouble(DataHelper.OUTPUTMB, reactor.isOn ? output : 0);
				FluidInfo.addTank(DataHelper.TANK, tag, reactor.tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, reactor.tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, reactor.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachinePlasmaHeater) {
				TileEntityMachinePlasmaHeater heater = (TileEntityMachinePlasmaHeater) base;
				tag.setLong(DataHelper.ENERGY, heater.getPower());
				tag.setLong(DataHelper.CAPACITY, heater.maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, heater.tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, heater.tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, heater.plasma);
				return tag;
			}
			if (base instanceof TileEntityMachineOrbus) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineOrbus) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineFractionTower) {
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineFractionTower) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineFractionTower) base).tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineFractionTower) base).tanks[2]);
				return tag;
			}
			if (base instanceof TileEntityMachineCrystallizer) {
				//tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineCrystallizer) base).canProcess());
				tag.setDouble(DataHelper.CONSUMPTION, ((TileEntityMachineCrystallizer) base).getPowerRequired());
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineCrystallizer) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineCrystallizer) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineCrystallizer) base).tank);
				return tag;
			}
			if (base instanceof TileEntityTowerLarge) {
				ArrayList values = getHookValues(base);
				if (values != null) {
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(0));
				}
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityTowerLarge) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityTowerLarge) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityTowerSmall) {
				ArrayList values = getHookValues(base);
				if (values != null) {
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(0));
				}
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityTowerSmall) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityTowerSmall) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityChungus) {
				TileEntityChungus engine = (TileEntityChungus) base;
				ArrayList values = getHookValues(engine);
				if (values != null) {
					tag.setBoolean(DataHelper.ACTIVE, (int) values.get(1) > 0);
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUT, (int) values.get(1));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(2));
				}
				tag.setLong(DataHelper.ENERGY, engine.getSPower());
				tag.setLong(DataHelper.CAPACITY, engine.maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, engine.tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, engine.tanks[1]);
				return tag;
			}
		}
		if (te instanceof TileEntityDummy) {
			TileEntity base = te.getWorld().getTileEntity(((TileEntityDummy) te).target);
			if (base instanceof TileEntityMachineChemplant) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineChemplant) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineChemplant) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineChemplant) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineChemplant) base).tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineChemplant) base).tanks[2]);
				FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineChemplant) base).tanks[3]);
				return tag;
			}
			if (base instanceof TileEntityMachineGasFlare) {
				NBTTagCompound tag = new NBTTagCompound();
				if (((TileEntityMachineGasFlare) te).tank.getFluidAmount() >= 10) {
					tag.setBoolean(DataHelper.ACTIVE, true);
					tag.setDouble(DataHelper.CONSUMPTION, 10);
					tag.setDouble(DataHelper.OUTPUT, 50);
				} else {
					tag.setBoolean(DataHelper.ACTIVE, false);
					tag.setDouble(DataHelper.CONSUMPTION, 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineGasFlare) te).getSPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineGasFlare) te).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineGasFlare) te).tank);
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
					tag.setBoolean(DataHelper.ACTIVE, true);
					tag.setDouble(DataHelper.CONSUMPTION, cnsp);
					tag.setDouble(DataHelper.OUTPUT, nrg);
				} else {
					tag.setBoolean(DataHelper.ACTIVE, false);
					tag.setDouble(DataHelper.CONSUMPTION, 0);
					tag.setDouble(DataHelper.OUTPUT, 0);
				}
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineTurbofan) base).getSPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineTurbofan) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineTurbofan) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineRadGen) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityMachineRadGen) base).strength > 0);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityMachineRadGen) base).strength);
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRadGen) base).getSPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRadGen) base).maxPower);
				tag.setInteger("fuel", ((TileEntityMachineRadGen) base).fuel);
				return tag;
			}
			if (base instanceof TileEntityAMSLimiter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityAMSLimiter) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSLimiter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSLimiter) base).heat);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityAMSLimiter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityAMSBase) {
				NBTTagCompound tag = new NBTTagCompound();
				TileEntityAMSBase reactor = (TileEntityAMSBase) base;
				double output = calcASMBaseOutput(reactor);
				tag.setBoolean(DataHelper.ACTIVE, output > 0);
				tag.setDouble(DataHelper.OUTPUT, output);
				tag.setLong(DataHelper.ENERGY, reactor.getSPower());
				tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
				tag.setLong("heat", reactor.heat);
				FluidInfo.addTank(DataHelper.TANK, tag, reactor.tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, reactor.tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, reactor.tanks[2]);
				FluidInfo.addTank(DataHelper.TANK4, tag, reactor.tanks[3]);
				return tag;
			}
			if (base instanceof TileEntityAMSEmitter) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityAMSEmitter) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityAMSEmitter) base).maxPower);
				tag.setLong("heat", ((TileEntityAMSEmitter) base).heat);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityAMSEmitter) base).tank);
				return tag;
			}
			if (base instanceof TileEntityMachineOilWell) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineOilWell) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineOilWell) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineOilWell) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineOilWell) base).tanks[1]);
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
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachinePumpjack) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachinePumpjack) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachinePumpjack) base).tanks[1]);
				return tag;
			}
			if (base instanceof TileEntityMachineRefinery) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setLong(DataHelper.ENERGY, ((TileEntityMachineRefinery) base).getPower());
				tag.setLong(DataHelper.CAPACITY, ((TileEntityMachineRefinery) base).maxPower);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineRefinery) base).tanks[0]);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityMachineRefinery) base).tanks[1]);
				FluidInfo.addTank(DataHelper.TANK3, tag, ((TileEntityMachineRefinery) base).tanks[2]);
				FluidInfo.addTank(DataHelper.TANK4, tag, ((TileEntityMachineRefinery) base).tanks[3]);
				FluidInfo.addTank(DataHelper.TANK5, tag, ((TileEntityMachineRefinery) base).tanks[4]);
				return tag;
			}
			if (base instanceof TileEntityMachineFluidTank) {
				NBTTagCompound tag = new NBTTagCompound();
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityMachineFluidTank) base).tank);
				return tag;
			}
		}
		if (te instanceof TileEntityMachineReactorSmall) {
			TileEntityMachineReactorSmall reactor = (TileEntityMachineReactorSmall) te;
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(DataHelper.ACTIVE, reactor.hasCoreHeat());
			double steam = reactor.hullHeat * 1.085D / 100000.0D * reactor.tanks[2].getCapacity() / 50.0D * getSmallReactorConversion(reactor);
			double water = steam;
			if (reactor.tankTypes[2] == ModForgeFluids.steam)
				water /= 100.0D;
			else if (reactor.tankTypes[2] == ModForgeFluids.hotsteam)
				water /= 10.0D;
			tag.setDouble(DataHelper.CONSUMPTION, (int) Math.ceil(water));
			tag.setDouble(DataHelper.OUTPUTMB, (int) Math.floor(steam));
			tag.setLong("core", Math.round(reactor.coreHeat * 2.0E-5D * 980.0D + 20.0D));
			tag.setLong("hull", Math.round(reactor.hullHeat * 1.0E-5D * 980.0D + 20.0D));
			FluidInfo.addTank(DataHelper.TANK, tag, reactor.tanks[0]);
			FluidInfo.addTank(DataHelper.TANK2, tag, reactor.tanks[1]);
			FluidInfo.addTank(DataHelper.TANK3, tag, reactor.tanks[2]);
			return tag;
		}
		if (te instanceof TileEntityReactorHatch) {
			for (EnumFacing side : EnumFacing.HORIZONTALS) {
				TileEntity core = te.getWorld().getTileEntity(te.getPos().offset(side, 2));
				if (core instanceof TileEntityMachineReactorLarge) {
					NBTTagCompound tag = new NBTTagCompound();
					TileEntityMachineReactorLarge reactor = (TileEntityMachineReactorLarge) core;
					tag.setBoolean(DataHelper.ACTIVE, reactor.rods > 0 && reactor.fuel > 0);
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
					tag.setDouble(DataHelper.CONSUMPTION, (int) Math.ceil(water));
					tag.setDouble(DataHelper.OUTPUTMB, (int) Math.floor(steam));
					FluidInfo.addTank(DataHelper.TANK, tag, reactor.tanks[0]);
					FluidInfo.addTank(DataHelper.TANK2, tag, reactor.tanks[1]);
					FluidInfo.addTank(DataHelper.TANK3, tag, reactor.tanks[2]);
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
					tag.setBoolean(DataHelper.ACTIVE, reactor.powerList > 0);
					tag.setLong(DataHelper.ENERGY, reactor.getSPower());
					tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
					tag.setDouble(DataHelper.OUTPUT, reactor.powerList);
					tag.setLong("heat", reactor.heatList);
					FluidInfo.addTank(DataHelper.TANK, tag, reactor.tank);
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
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setLong(DataHelper.ENERGY, reactor.getSPower());
					tag.setLong(DataHelper.CAPACITY, reactor.maxPower);
					tag.setDouble(DataHelper.CONSUMPTION, active ? 20 : 0);
					tag.setDouble(DataHelper.OUTPUT, active ? reactor.isCoatingValid(te.getWorld()) ? 200000 : 100000 : 0);
					FluidInfo.addTank(DataHelper.TANK, tag, reactor.tanks[0]);
					FluidInfo.addTank(DataHelper.TANK2, tag, reactor.tanks[1]);
					FluidInfo.addTank(DataHelper.TANK3, tag, reactor.tanks[2]);
					return tag;
				}
			}
		}
		if (te instanceof TileEntityRBMKBase) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("heatD", ((TileEntityRBMKBase) te).heat);
			if (te instanceof TileEntityRBMKBoiler) {
				ArrayList values = getHookValues(te);
				if (values != null) {
					tag.setDouble(DataHelper.CONSUMPTION, (int) values.get(0));
					tag.setDouble(DataHelper.OUTPUTMB, (int) values.get(1));
				}
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityRBMKBoiler) te).feed);
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityRBMKBoiler) te).steam);
			}
			if (te instanceof TileEntityRBMKRod) {
				TileEntityRBMKRod rod = (TileEntityRBMKRod) te;
				ItemStack stack = rod.inventory.getStackInSlot(0);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemRBMKRod) {
					tag.setDouble("fluxFast", rod.fluxFast);
					tag.setDouble("fluxSlow", rod.fluxSlow);
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
			tag.setLong(DataHelper.CAPACITY, ((TileEntityCoreTitanium) te).maxPower);
			return tag;
		}
		if (te instanceof TileEntityGeiger) {
			NBTTagCompound tag = new NBTTagCompound();
			RadiationSavedData data = RadiationSavedData.getData(te.getWorld());
			double rads = (int) (data.getRadNumFromCoord(te.getPos()) * 10.0F) / 10.0D;
			String chunkPrefix = ContaminationUtil.getPreffixFromRad(rads);
			tag.setString("chunkRad", chunkPrefix + rads + " RAD/s");
			return tag;
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

	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = HBMHooks.map.get(te);
		if (values == null)
			HBMHooks.map.put(te, null);
		return values;
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
