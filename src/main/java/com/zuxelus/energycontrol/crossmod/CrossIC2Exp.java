package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.OreHelper;
import com.zuxelus.energycontrol.blocks.AFSU;
import com.zuxelus.energycontrol.blocks.SeedAnalyzer;
import com.zuxelus.energycontrol.blocks.SeedLibrary;
import com.zuxelus.energycontrol.hooks.IC2Hooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemAFB;
import com.zuxelus.energycontrol.items.ItemAFSU;
import com.zuxelus.energycontrol.items.ItemAFSUUpgradeKit;
import com.zuxelus.energycontrol.items.ItemDigitalThermometer;
import com.zuxelus.energycontrol.items.cards.ItemCardIC2;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitIC2;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedAnalyzer;
import com.zuxelus.energycontrol.tileentities.TileEntitySeedLibrary;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.ICustomDamageItem;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Fluids;
import ic2.core.block.generator.tileentity.*;
import ic2.core.block.heatgenerator.tileentity.*;
import ic2.core.block.kineticgenerator.tileentity.*;
import ic2.core.block.machine.tileentity.*;
import ic2.core.block.reactor.tileentity.*;
import ic2.core.block.type.ResourceBlock;
import ic2.core.init.MainConfig;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.profile.ProfileManager;
import ic2.core.profile.Version;
import ic2.core.ref.BlockName;
import ic2.core.util.Config;
import ic2.core.util.ConfigUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrossIC2Exp extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("upgrade", "transformer");
		case "energy_storage":
			return IC2Items.getItem("upgrade", "energy_storage");
		case "machine":
			return IC2Items.getItem("resource", "machine");
		case "mfsu":
			return IC2Items.getItem("te","mfsu");
		case "circuit":
			return IC2Items.getItem("crafting","circuit");
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getChargedStack(ItemStack stack) {
		ElectricItem.manager.charge(stack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		return stack;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isElectricItem(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof IElectricItem;
	}

	@Override
	public double dischargeItem(ItemStack stack, double needed) {
		IElectricItem ielectricitem = (IElectricItem) stack.getItem();
		if (ielectricitem.canProvideEnergy(stack))
			return ElectricItem.manager.discharge(stack, needed, 1, false, false, false);
		return 0;
	}

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "EU");
		if (te instanceof IEnergyStorage) {
			tag.setDouble(DataHelper.ENERGY, ((IEnergyStorage) te).getStored());
			tag.setDouble(DataHelper.CAPACITY, ((IEnergyStorage) te).getCapacity());
			return tag;
		}
		if (te instanceof TileEntityBaseGenerator) {
			Energy energy = ((TileEntityBaseGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntitySemifluidGenerator) {
			Energy energy = ((TileEntitySemifluidGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityStirlingGenerator) {
			Energy energy = ((TileEntityStirlingGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityGeoGenerator) {
			Energy energy = ((TileEntityGeoGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityElectricKineticGenerator) {
			Energy energy = (Energy) ((TileEntityElectricKineticGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityElectricHeatGenerator) {
			Energy energy = (Energy) ((TileEntityElectricHeatGenerator) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityCondenser) {
			Energy energy = (Energy) ((TileEntityCondenser) te).getComponent(Energy.class);
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (!(te instanceof TileEntityBlock))
			return null;

		if (!((TileEntityBlock)te).hasComponent(Fluids.class))
			return null;

		Fluids fluid = ((TileEntityBlock)te).getComponent(Fluids.class);

		List<FluidInfo> result = new ArrayList<>();
		for (FluidTank tank: fluid.getAllTanks())
			result.add(new FluidInfo(tank));

		return result;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			if (te instanceof IEnergyStorage) {
				tag.setDouble(DataHelper.ENERGY, ((IEnergyStorage) te).getStored());
				tag.setDouble(DataHelper.CAPACITY, ((IEnergyStorage) te).getCapacity());
				ArrayList values = getHookValues(te);
				if (values != null)
					tag.setDouble(DataHelper.DIFF, ((Double) values.get(0) - (Double) values.get(20)) / 20);
				return tag;
			}

			// GeneratorData
			if (te instanceof TileEntityBaseGenerator) {
				Energy energy = ((TileEntityBaseGenerator) te).getComponent(Energy.class);
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				if (te instanceof TileEntityGenerator) {
					tag.setDouble(DataHelper.OUTPUT, isActive(te) ? DataHelper.getDouble(TileEntityBaseGenerator.class, "production", te) : 0);
					return tag;
				}
				if (te instanceof TileEntityRTGenerator) {
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTGenerator) te).fuelSlot.size(); i++)
						if (!((TileEntityRTGenerator) te).fuelSlot.isEmpty(i))
							counter++;
					tag.setInteger("pellets", counter);
					if (counter == 0 || energy.getEnergy() >= energy.getCapacity()) {
						tag.setBoolean(DataHelper.ACTIVE, false);
						tag.setDouble(DataHelper.OUTPUT, 0);
						return tag;
					}
					tag.setBoolean(DataHelper.ACTIVE, true);
					double efficiency = DataHelper.getFloat(TileEntityRTGenerator.class, "efficiency", te);
					tag.setDouble(DataHelper.MULTIPLIER, efficiency);
					tag.setDouble(DataHelper.OUTPUT, Math.pow(2.0D, (counter - 1)) * efficiency);
					return tag;
				}
				if (te instanceof TileEntitySolarGenerator) {
					double multiplier = DataHelper.getDouble(TileEntitySolarGenerator.class, "energyMultiplier", te);
					boolean active = ((TileEntitySolarGenerator) te).skyLight > 0 && energy.getEnergy() < energy.getCapacity();
					tag.setDouble(DataHelper.MULTIPLIER, multiplier);
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setDouble(DataHelper.OUTPUT, active ? multiplier * ((TileEntitySolarGenerator) te).skyLight : 0);
					return tag;
				}
				if (te instanceof TileEntityWaterGenerator) {
					boolean active = ((TileEntityWaterGenerator) te).water > 0 || ((TileEntityWaterGenerator) te).fuel > 0;
					double multiplier = DataHelper.getDouble(TileEntityWaterGenerator.class, "energyMultiplier", te);
					tag.setDouble(DataHelper.MULTIPLIER, multiplier);
					tag.setBoolean(DataHelper.ACTIVE, active);
					if (((TileEntityWaterGenerator) te).fuel <= 0)
						tag.setDouble(DataHelper.OUTPUT, multiplier * ((TileEntityWaterGenerator) te).water / 100);
					else
						tag.setDouble(DataHelper.OUTPUT, DataHelper.getDouble(TileEntityBaseGenerator.class, "production", te));
					return tag;
				}
				if (te instanceof TileEntityWindGenerator) {
					double production = DataHelper.getDouble(TileEntityBaseGenerator.class, "production", te);
					boolean active = production > 0 && energy.getEnergy() < 4;
					double multiplier = DataHelper.getDouble(TileEntityWindGenerator.class, "energyMultiplier", te);
					tag.setDouble(DataHelper.MULTIPLIER, multiplier * 0.1D);
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setDouble(DataHelper.OUTPUT, active ? production : 0);
					int obstructedBlocks = DataHelper.getInt(TileEntityWindGenerator.class, "obstructedBlockCount", te);
					tag.setInteger("height", te.getPos().getY());
					tag.setInteger("obstructedBlocks", obstructedBlocks);
					tag.setDouble("wind", WorldData.get(te.getWorld()).windSim.getWindAt(te.getPos().getY()) * (1.0D - obstructedBlocks / 567.0D));
					return tag;
				}
			}

			if (te instanceof TileEntitySemifluidGenerator) {
				Energy energy = ((TileEntitySemifluidGenerator) te).getComponent(Energy.class);
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				tag.setDouble(DataHelper.OUTPUT, ((TileEntitySemifluidGenerator) te).isConverting() ? DataHelper.getDouble(TileEntityBaseGenerator.class, "production", te) : 0);
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySemifluidGenerator) te).isConverting());
				Fluids fluids = ((TileEntitySemifluidGenerator) te).getComponent(Fluids.class);
				FluidInfo.addTank(DataHelper.TANK, tag, fluids.getFluidTank("fluid"));
				return tag;
			}

			if (te instanceof TileEntityStirlingGenerator) {
				double production = DataHelper.getDouble(TileEntityConversionGenerator.class, "lastProduction", te);
				tag.setBoolean(DataHelper.ACTIVE, production > 0);
				tag.setDouble(DataHelper.OUTPUT, production);
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getDouble(TileEntityStirlingGenerator.class, "productionpeerheat", te));
				return tag;
			}

			if (te instanceof TileEntityGeoGenerator) {
				Energy energy = ((TileEntityGeoGenerator) te).getComponent(Energy.class);
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				boolean active = ((TileEntityGeoGenerator) te).getActive();
				tag.setBoolean(DataHelper.ACTIVE, active);
				tag.setDouble(DataHelper.OUTPUT, active ? DataHelper.getDouble(TileEntityBaseGenerator.class, "production", te) : 0);
				Fluids fluids = ((TileEntityGeoGenerator) te).getComponent(Fluids.class);
				FluidInfo.addTank(DataHelper.TANK, tag, fluids.getFluidTank("fluid"));
				return tag;
			}

			if (te instanceof TileEntityKineticGenerator) {
				boolean active = ((TileEntityKineticGenerator)te).getActive();
				tag.setBoolean(DataHelper.ACTIVE, active);
				tag.setDouble(DataHelper.OUTPUT, DataHelper.getDouble(TileEntityConversionGenerator.class, "lastProduction", te));
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getDouble(TileEntityKineticGenerator.class, "euPerKu", te));
				return tag;
			}

			// KineticData
			if (te instanceof TileEntityElectricKineticGenerator) {
				TileEntityElectricKineticGenerator generator = ((TileEntityElectricKineticGenerator) te);
				int counter = 0;
				for (int i = 0; i < generator.slotMotor.size(); i++)
					if (!generator.slotMotor.get(i).isEmpty())
						counter++;
				tag.setInteger("motors", counter);
				Energy energy = (Energy) generator.getComponent(Energy.class);
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				tag.setBoolean(DataHelper.ACTIVE, energy.getEnergy() > 0 && counter > 0);
				tag.setDouble(DataHelper.ENERGYKU, generator.ku);
				tag.setDouble(DataHelper.CAPACITYKU, generator.maxKU);
				ArrayList values = getHookValues(te);
				if (values != null)
					tag.setDouble(DataHelper.OUTPUTKU, (double) values.get(0));
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getFloat(TileEntityElectricKineticGenerator.class, "kuPerEU", te));
				return tag;
			}
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			if (te instanceof TileEntitySteamKineticGenerator) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityBlock) te).getActive());
				tag.setDouble(DataHelper.OUTPUTKU, ((TileEntitySteamKineticGenerator) te).getKUoutput());
				Field field = TileEntitySteamKineticGenerator.class.getDeclaredField("steamTank");
				field.setAccessible(true);
				FluidInfo.addTank(DataHelper.TANK, tag, (FluidTank) field.get(te));
				return tag;
			}
			if (te instanceof TileEntityStirlingKineticGenerator) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityBlock)te).getActive());
				tag.setDouble(DataHelper.ENERGYKU, DataHelper.getInt(TileEntityStirlingKineticGenerator.class, "kUBuffer", te));
				tag.setDouble(DataHelper.CAPACITYKU, DataHelper.getInt(TileEntityStirlingKineticGenerator.class, "maxkUBuffer", te));
				tag.setDouble(DataHelper.ENERGYHU, DataHelper.getInt(TileEntityStirlingKineticGenerator.class, "heatbuffer", te));
				tag.setDouble(DataHelper.CAPACITYHU, DataHelper.getInt(TileEntityStirlingKineticGenerator.class, "maxHeatbuffer", te));
				tag.setDouble(DataHelper.MULTIPLIER, 3);
				return tag;
			}
			if (te instanceof TileEntityWaterKineticGenerator) {
				TileEntityWaterKineticGenerator generator = ((TileEntityWaterKineticGenerator) te);
				tag.setDouble(DataHelper.OUTPUTKU, generator.getKuOutput());
				tag.setDouble("waterFlow", DataHelper.getInt(TileEntityWaterKineticGenerator.class, "waterFlow", te));
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getFloat(TileEntityWaterKineticGenerator.class, "outputModifier", te));
				tag.setInteger("height", generator.getPos().getY());
				 if (generator.rotorSlot.isEmpty())
					 tag.setDouble("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - generator.rotorSlot.get().getItemDamage() * 100.0F / generator.rotorSlot.get().getMaxDamage()));
				return tag;
			}
			if (te instanceof TileEntityWindKineticGenerator) {
				TileEntityWindKineticGenerator generator = ((TileEntityWindKineticGenerator) te);
				tag.setDouble(DataHelper.OUTPUTKU, generator.getKuOutput());
				tag.setDouble("wind", DataHelper.getDouble(TileEntityWindKineticGenerator.class, "windStrength", te));
				tag.setDouble(DataHelper.MULTIPLIER, generator.getEfficiency() * TileEntityWindKineticGenerator.outputModifier);
				tag.setInteger("height", generator.getPos().getY());
				 if (generator.rotorSlot.isEmpty())
					 tag.setDouble("health", -1);
				 else
					 tag.setDouble("health", (double)(100.0F - generator.rotorSlot.get().getItemDamage() * 100.0F / generator.rotorSlot.get().getMaxDamage()));
				return tag;
			}

			// Heat Data
			if (te instanceof TileEntityHeatSourceInventory) {
				int buffer = ((TileEntityHeatSourceInventory) te).getHeatBuffer();
				tag.setInteger(DataHelper.ENERGYHU, buffer);
				if (te instanceof TileEntityElectricHeatGenerator) {
					Energy energy = (Energy) ((TileEntityElectricHeatGenerator) te).getComponent(Energy.class);
					tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
					tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
					int counter = 0;
					for (int i = 0; i < ((TileEntityElectricHeatGenerator) te).coilSlot.size(); i++)
						if (!((TileEntityElectricHeatGenerator) te).coilSlot.get(i).isEmpty())
							counter++;
					tag.setInteger("coils", counter);

					boolean active = ((TileEntityHeatSourceInventory)te).getActive();
					tag.setBoolean(DataHelper.ACTIVE, active);
					//tag.setDouble("consumptionEU", active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getDouble(TileEntityElectricHeatGenerator.class, "outputMultiplier", te));
					return tag;
				}
				if (te instanceof TileEntityFluidHeatGenerator) {
					boolean active = DataHelper.getInt(TileEntityFluidHeatGenerator.class, "production", te) > 0;
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityFluidHeatGenerator) te).getFluidTank());
					return tag;
				}
				if (te instanceof TileEntityLiquidHeatExchanger) {
					FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityLiquidHeatExchanger) te).inputTank);
					FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityLiquidHeatExchanger) te).outputTank);
					int counter = 0;
					for (int i = 0; i < ((TileEntityLiquidHeatExchanger) te).heatexchangerslots.size(); i++)
						if (((TileEntityLiquidHeatExchanger) te).heatexchangerslots.get(i) != null)
							counter++;
					boolean active = buffer == 0;
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					tag.setInteger("conductors", counter);
					return tag;
				}
				if (te instanceof TileEntityRTHeatGenerator) {
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTHeatGenerator) te).fuelSlot.size(); i++)
						if (!((TileEntityRTHeatGenerator) te).fuelSlot.get(i).isEmpty())
							counter++;
					tag.setInteger("pellets", counter);
					boolean active = counter > 0;
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					tag.setDouble(DataHelper.MULTIPLIER, TileEntityRTHeatGenerator.outputMultiplier);
					return tag;
				}
				if (te instanceof TileEntitySolidHeatGenerator) {
					boolean active = ((TileEntityBlock) te).getActive();
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					tag.setInteger("energy", DataHelper.getInt(TileEntitySolidHeatGenerator.class, "heatbuffer", te) + ((TileEntitySolidHeatGenerator) te).getHeatBuffer());
					return tag;
				}
			}

			if (te instanceof TileEntitySteamGenerator) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySteamGenerator) te).getHeatInput() > 0);
				tag.setDouble("heatD", ((TileEntitySteamGenerator) te).getSystemHeat());
				tag.setInteger(DataHelper.OUTPUTMB, ((TileEntitySteamGenerator) te).getOutputMB());
				tag.setInteger(DataHelper.CONSUMPTION, ((TileEntitySteamGenerator) te).getInputMB());
				tag.setInteger("heatChange", ((TileEntitySteamGenerator) te).getHeatInput());
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntitySteamGenerator) te).waterTank);
				tag.setDouble("calcification", ((TileEntitySteamGenerator) te).getCalcification());
				tag.setInteger("pressure", ((TileEntitySteamGenerator) te).getPressure());
				return tag;
			}
			if (te instanceof TileEntityCondenser) {
				tag.setBoolean(DataHelper.ACTIVE, true);
				Energy energy = (Energy) ((TileEntityCondenser) te).getComponent(Energy.class);
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				tag.setDouble("progress", ((TileEntityCondenser) te).progress * 100.0D / ((TileEntityCondenser) te).maxProgress);
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntityCondenser) te).getInputTank());
				FluidInfo.addTank(DataHelper.TANK2, tag, ((TileEntityCondenser) te).getOutputTank());
				return tag;
			}
			if (te instanceof TileEntityReactorChamberElectric)
				return getReactorData(((TileEntityReactorChamberElectric) te).getReactorInstance());
			if (te instanceof TileEntityNuclearReactorElectric)
				return getReactorData((TileEntityNuclearReactorElectric) te);
			if (te instanceof TileEntityReactorVessel)
				return getReactorData(((TileEntityReactorVessel) te).getReactorInstance());
		} catch (Throwable t) { }
		return null;
	}

	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator || te instanceof TileEntityStirlingGenerator || te instanceof TileEntitySolarGenerator)
			return ((TileEntityBlock)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}

	private NBTTagCompound getReactorData(TileEntityNuclearReactorElectric reactor) {
		if (reactor == null)
			return null;
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger(DataHelper.HEAT, reactor.getHeat());
		tag.setInteger(DataHelper.MAXHEAT, reactor.getMaxHeat());
		tag.setBoolean(DataHelper.ACTIVE, reactor.produceEnergy());
		if (reactor.isFluidCooled())
			tag.setDouble("outputmB", reactor.EmitHeat);
		else
			tag.setDouble(DataHelper.OUTPUT, reactor.getReactorEUEnergyOutput());

		IInventory inventory = (IInventory) reactor;
		int slotCount = inventory.getSizeInventory();
		int dmgLeft = 0;
		for (int i = 0; i < slotCount; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null)
				dmgLeft = Math.max(dmgLeft, getNuclearCellTimeLeft(stack));
		}

		tag.setInteger("timeLeft", dmgLeft * reactor.getTickRate() / 20);
		FluidInfo.addTank(DataHelper.TANK, tag, reactor.inputTank);
		FluidInfo.addTank(DataHelper.TANK2, tag, reactor.outputTank);
		return tag;
	}

	private static int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack.isEmpty())
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		// Coaxium Mod
		if (item.getClass().getName().equals("com.sm.FirstMod.items.ItemCoaxiumRod") || item.getClass().getName().equals("com.sm.FirstMod.items.ItemCesiumRod"))
			return stack.getMaxDamage() - getCoaxiumDamage(stack);
		return 0;
	}

	private static int getCoaxiumDamage(ItemStack stack) {
		if (!stack.hasTagCompound())
			return 0;
		return stack.getTagCompound().getInteger("fuelRodDamage");
	}

	@Override
	public int getHeat(World world, BlockPos pos) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, pos);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = IC2Hooks.map.get(te);
		if (values == null)
			IC2Hooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerBlocks(Register<Block> event) {
		if (getProfile() == 0)
			ModItems.blockAfsu = ModItems.register(event, new AFSU(), "afsu");
		ModItems.blockSeedAnalyzer = ModItems.register(event, new SeedAnalyzer(), "seed_analyzer");
		ModItems.blockSeedLibrary = ModItems.register(event, new SeedLibrary(), "seed_library");
	}

	@Override
	public void registerItems(Register<Item> event) {
		if (getProfile() == 0)
			event.getRegistry().register(new ItemAFSU(ModItems.blockAfsu).setRegistryName("afsu"));
		event.getRegistry().register(new ItemBlock(ModItems.blockSeedAnalyzer).setRegistryName("seed_analyzer"));
		event.getRegistry().register(new ItemBlock(ModItems.blockSeedLibrary).setRegistryName("seed_library"));

		ModItems.itemThermometerDigital = ModItems.register(event, new ItemDigitalThermometer(), "thermometer_digital");
		if (getProfile() == 0) {
			ModItems.itemAFB = ModItems.register(event, new ItemAFB(), "afb");
			ModItems.itemAFSUUpgradeKit = ModItems.register(event, new ItemAFSUUpgradeKit(), "afsu_upgrade_kit");
		}
		ItemKitMain.register(ItemKitIC2::new);
		ItemCardMain.register(ItemCardIC2::new);
	}

	@Override
	public void registerModels(ModelRegistryEvent event) {
		if (getProfile() == 0)
			ModItems.registerBlockModel(ModItems.blockAfsu, 0, "afsu");
		ModItems.registerBlockModel(ModItems.blockSeedAnalyzer, 0, "seed_analyzer");
		ModItems.registerBlockModel(ModItems.blockSeedLibrary, 0, "seed_library");

		ModItems.registerItemModel(ModItems.itemThermometerDigital, 0, "thermometer_digital");
		if (getProfile() == 0) {
			ModItems.registerItemModel(ModItems.itemAFB, 0, "afb");
			ModItems.registerItemModel(ModItems.itemAFSUUpgradeKit, 0, "afsu_upgrade_kit");
		}
	}

	@Override
	public void registerTileEntities() {
		if (getProfile() == 0)
			GameRegistry.registerTileEntity(TileEntityAFSU.class, EnergyControl.MODID + ":afsu");
		GameRegistry.registerTileEntity(TileEntitySeedAnalyzer.class, EnergyControl.MODID + ":seed_analyzer");
		GameRegistry.registerTileEntity(TileEntitySeedLibrary.class, EnergyControl.MODID + ":seed_library");
	}

	public int getProfile() {
		return ProfileManager.selected.style == Version.OLD ? 1 : 0; 
	}

	@Override
	public void loadOreInfo() {
		Config config = MainConfig.get().getSub("worldgen");
		loadOre(BlockName.resource.getBlockState(ResourceBlock.copper_ore).getBlock(), 1,config.getSub("copper"));
		loadOre(BlockName.resource.getBlockState(ResourceBlock.lead_ore).getBlock(), 2,config.getSub("lead"));
		loadOre(BlockName.resource.getBlockState(ResourceBlock.tin_ore).getBlock(), 3,config.getSub("tin"));
		loadOre(BlockName.resource.getBlockState(ResourceBlock.uranium_ore).getBlock(), 4,config.getSub("uranium"));
	}

	private void loadOre(Block block, int meta, Config config) {
		EnergyControl.oreHelper.put(OreHelper.getId(block, meta),
			new OreHelper(ConfigUtil.getInt(config, "minHeight"), ConfigUtil.getInt(config, "maxHeight"), ConfigUtil.getInt(config, "size"), ConfigUtil.getInt(config, "count")));
	}
}
