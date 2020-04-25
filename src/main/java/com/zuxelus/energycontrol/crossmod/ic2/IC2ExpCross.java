package com.zuxelus.energycontrol.crossmod.ic2;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.item.ICustomDamageItem;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Fluids;
import ic2.core.block.comp.Fluids.InternalFluidTank;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityConversionGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityManualKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityStirlingKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class IC2ExpCross extends IC2Cross {

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell
				|| item instanceof ItemReactorMOX)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);

		return 0;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.EXP;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isSteamReactor(TileEntity par1) {
		return false;
	}

	@Override
	public EnergyStorageData getEnergyStorageData(TileEntity target) {
		if (target instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) target;
			EnergyStorageData result = new EnergyStorageData();
			result.values.add((double) storage.getCapacity());
			result.values.add((double) storage.getStored());
			result.type = EnergyStorageData.TARGET_TYPE_IC2;
			return result;
		}
		return null;
	}

	@Override
	public ReactorInfo getReactorInfo(TileEntity par1) {
		if (par1 == null || !(par1 instanceof TileEntityNuclearReactorElectric))
			return null;
		TileEntityNuclearReactorElectric reactor = (TileEntityNuclearReactorElectric) par1;
		ReactorInfo info = new ReactorInfo();
		info.isOnline = reactor.getActive();
		info.outTank = reactor.getoutputtank().getFluidAmount();
		info.inTank = reactor.getinputtank().getFluidAmount();
		info.emitHeat = reactor.EmitHeat;
		info.coreTemp = (int) (((double) reactor.getHeat() / (double) reactor.getMaxHeat()) * 100D);
		return info;
	}

	@Override
	public boolean isMultiReactorPart(TileEntity par1) {
		if (par1 instanceof TileEntityReactorRedstonePort || par1 instanceof TileEntityReactorAccessHatch)
			return true;
		return false;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = isActive(te);
			tag.setBoolean("active", active);
			if (te instanceof TileEntityBaseGenerator) {
				tag.setInteger("type", 1);
				Energy energy = ((TileEntityBaseGenerator) te).getComponent(Energy.class);
				tag.setDouble("storage", energy.getEnergy());
				tag.setDouble("maxStorage", energy.getCapacity());
				if (te instanceof TileEntitySolarGenerator) {
					float light = ((TileEntitySolarGenerator)te).skyLight;
					tag.setBoolean("active", light > 0);
					tag.setDouble("production", (double) light);
					return tag;
				}
				if (te instanceof TileEntityRTGenerator) {
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTGenerator) te).fuelSlot.size(); i++)
						if (!((TileEntityRTGenerator) te).fuelSlot.isEmpty(i))
							counter++;
					if (counter == 0 || energy.getEnergy() >= energy.getCapacity()) {
						tag.setBoolean("active", false);
						tag.setDouble("production", 0);
						return tag;
					}
					tag.setBoolean("active", true);
					Field field = TileEntityRTGenerator.class.getDeclaredField("efficiency");
					field.setAccessible(true);
					tag.setDouble("production", (double) Math.pow(2.0D, (counter - 1)) * (float) field.get(te));
					return tag;
				}
				if (te instanceof TileEntityWaterGenerator) {
					active = ((TileEntityWaterGenerator)te).water > 0 || ((TileEntityWaterGenerator)te).fuel > 0;
					tag.setBoolean("active", active);
					if (((TileEntityWaterGenerator) te).fuel <= 0) {
						Field field = TileEntityWaterGenerator.class.getDeclaredField("energyMultiplier");
						field.setAccessible(true);
						tag.setDouble("production", (Double) field.get(te) * ((TileEntityWaterGenerator) te).water / 100);
						return tag;
					}
				}
				if (active) {
					Field field = TileEntityBaseGenerator.class.getDeclaredField("production");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				return tag;
			}
			
			if (te instanceof TileEntityConversionGenerator) {
				if (active) {
					Field field = TileEntityConversionGenerator.class.getDeclaredField("lastProduction");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				} else
					tag.setDouble("production", 0);
				if (te instanceof TileEntityStirlingGenerator) {
					tag.setInteger("type", 2);
					Field field = TileEntityStirlingGenerator.class.getDeclaredField("productionpeerheat");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				if (te instanceof TileEntityKineticGenerator) {
					tag.setInteger("type", 2);
					Field field = TileEntityKineticGenerator.class.getDeclaredField("euPerKu");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}
	
	@Override
	public NBTTagCompound getGeneratorKineticData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setInteger("type", 1);
				tag.setDouble("storage", ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble("maxStorage", ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			
			Boolean active = ((TileEntityBlock) te).getActive();
			if (te instanceof TileEntityWindKineticGenerator) {
				TileEntityWindKineticGenerator entity = ((TileEntityWindKineticGenerator) te);
				tag.setInteger("type", 2);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWindKineticGenerator.class.getDeclaredField("windStrength");
				field.setAccessible(true);
				tag.setDouble("wind", (Double) field.get(te));
				tag.setDouble("multiplier", entity.getEfficiency() * entity.outputModifier);
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			
			if (te instanceof TileEntityWaterKineticGenerator) {
				TileEntityWaterKineticGenerator entity = ((TileEntityWaterKineticGenerator) te);
				tag.setInteger("type", 2);
				tag.setDouble("output", entity.getKuOutput());
				Field field = TileEntityWaterKineticGenerator.class.getDeclaredField("waterFlow");
				field.setAccessible(true);
				tag.setDouble("wind", (Integer) field.get(te));
				field = TileEntityWaterKineticGenerator.class.getDeclaredField("outputModifier");
				field.setAccessible(true);
				tag.setDouble("multiplier", (double) (Float) field.get(te));
				tag.setInteger("height", entity.getPos().getY());
				 if (entity.rotorSlot.isEmpty())
					 tag.setInteger("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - entity.rotorSlot.get().getItemDamage() * 100.0F / entity.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			if (te instanceof TileEntityStirlingKineticGenerator) {
				//TODO
			}
		} catch (Throwable t) {
		}
		return null;
	}

	@Override
	public NBTTagCompound getGeneratorHeatData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			Boolean active = ((TileEntityBlock)te).getActive();
			tag.setBoolean("active", active);
			if (te instanceof TileEntityHeatSourceInventory) {
				tag.setInteger("type", 1);
				if (active)
					tag.setInteger("output", ((TileEntityHeatSourceInventory)te).gettransmitHeat());
				else
					tag.setInteger("output", 0);
				if (te instanceof TileEntityElectricHeatGenerator) {
					Energy energy = ((TileEntityHeatSourceInventory) te).getComponent(Energy.class);
					tag.setDouble("storage", energy.getEnergy());
					tag.setDouble("maxStorage", energy.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityElectricHeatGenerator) te).coilSlot)
						if (!stack.isEmpty())
							count++;
					tag.setInteger("coils", count);
				}
				if (te instanceof TileEntityLiquidHeatExchanger) {
					Fluids fluid = ((TileEntityLiquidHeatExchanger) te).getComponent(Fluids.class);
					Iterable<InternalFluidTank> tanks = fluid.getAllTanks();
					InternalFluidTank tank = tanks.iterator().next();
					tag.setDouble("storage", tank.getFluidAmount());
					tag.setDouble("maxStorage", tank.getCapacity());
					int count = 0;
					for (ItemStack stack : ((TileEntityLiquidHeatExchanger) te).heatexchangerslots)
						if (!stack.isEmpty())
							count++;
					tag.setInteger("coils", count);
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}
	
	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator || te instanceof TileEntityConversionGenerator || te instanceof TileEntitySolarGenerator)
			return ((TileEntityBlock)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}
}
