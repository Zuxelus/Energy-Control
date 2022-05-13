package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.blocks.AFSU;
import com.zuxelus.energycontrol.blocks.SeedAnalyzer;
import com.zuxelus.energycontrol.blocks.SeedLibrary;
import com.zuxelus.energycontrol.hooks.IC2Hooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.*;
import com.zuxelus.energycontrol.items.cards.ItemCardIC2;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitIC2;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.item.*;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.*;
import ic2.core.block.heatgenerator.tileentity.*;
import ic2.core.block.kineticgenerator.tileentity.*;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossIC2Exp extends CrossModBase {

	@Override
	public ItemStack getItemStack(String name) {
		switch (name) {
		case "transformer":
			return IC2Items.getItem("transformerUpgrade");
		case "mfsu":
			return IC2Items.getItem("mfsUnit");
		}
		return null;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isElectricItem(ItemStack stack) {
		return stack != null && stack.getItem() instanceof IElectricItem;
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
			tag.setDouble(DataHelper.ENERGY, ((TileEntityBaseGenerator) te).storage);
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityBaseGenerator) te).maxStorage);
			return tag;
		}
		if (te instanceof TileEntitySemifluidGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntitySemifluidGenerator) te).storage);
			tag.setDouble(DataHelper.CAPACITY, DataHelper.getShort(TileEntitySemifluidGenerator.class, "maxStorage", te));
			return tag;
		}
		if (te instanceof TileEntityStirlingGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityStirlingGenerator) te).EUstorage);
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityStirlingGenerator) te).maxEUStorage);
			return tag;
		}
		if (te instanceof TileEntityGeoGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityGeoGenerator) te).storage);
			tag.setDouble(DataHelper.CAPACITY, ((TileEntityGeoGenerator) te).maxStorage);
			return tag;
		}
		if (te instanceof TileEntityKineticGenerator) {
			tag.setDouble(DataHelper.ENERGY, ((TileEntityKineticGenerator)te).EUstorage);
			tag.setDouble(DataHelper.CAPACITY, DataHelper.getInt(TileEntityKineticGenerator.class, "maxEUStorage", te));
			return tag;
		}
		if (te instanceof TileEntityElectricKineticGenerator) {
			Energy energy = (Energy) ((TileEntityElectricKineticGenerator) te).getComponent("energy");
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		if (te instanceof TileEntityElectricHeatGenerator) {
			Energy energy = (Energy) ((TileEntityElectricHeatGenerator) te).getComponent("energy");
			tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
			return tag;
		}
		return null;
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
				tag.setDouble(DataHelper.ENERGY, ((TileEntityBaseGenerator) te).storage);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityBaseGenerator) te).maxStorage);
				if (te instanceof TileEntityGenerator) {
					tag.setDouble(DataHelper.OUTPUT, isActive(te) ? ((TileEntityBaseGenerator) te).production : 0);
					return tag;
				}
				if (te instanceof TileEntityRTGenerator) {
					int counter = 0;
					for (int i = 0; i < ((TileEntityRTGenerator) te).fuelSlot.size(); i++)
						if (((TileEntityRTGenerator) te).fuelSlot.get(i) != null)
							counter++;
					tag.setInteger("pellets", counter);
					if (counter == 0 || ((TileEntityBaseGenerator) te).storage >= ((TileEntityBaseGenerator) te).maxStorage) {
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
					double multiplier = ((TileEntitySolarGenerator) te).solarbasevalue;
					Boolean active = ((TileEntitySolarGenerator)te).sunIsVisible && ((TileEntityBaseGenerator) te).storage < ((TileEntityBaseGenerator) te).maxStorage;
					tag.setDouble(DataHelper.MULTIPLIER, multiplier);
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setDouble(DataHelper.OUTPUT, active ? multiplier * (te.getWorldObj().getBlockLightValue(te.xCoord, 255, te.zCoord) / 15.0F) : 0);
					return tag;
				}
				if (te instanceof TileEntityWaterGenerator) {
					Boolean active = (((TileEntityWaterGenerator) te).water > 0 || ((TileEntityWaterGenerator) te).fuel > 0) && ((TileEntityBaseGenerator) te).storage < 4;
					double multiplier = ((TileEntityWaterGenerator) te).waterbasevalue;
					tag.setDouble(DataHelper.MULTIPLIER, multiplier);
					tag.setBoolean(DataHelper.ACTIVE, active);
					if (((TileEntityWaterGenerator) te).fuel <= 0)
						tag.setDouble(DataHelper.OUTPUT, multiplier * ((TileEntityWaterGenerator) te).water / 100);
					else
						tag.setDouble(DataHelper.OUTPUT, 0);
					return tag;
				}
				if (te instanceof TileEntityWindGenerator) {
					Boolean active = ((TileEntityWindGenerator) te).subproduction > 0 && ((TileEntityBaseGenerator) te).storage < 4;
					double multiplier = ((TileEntityWindGenerator) te).windbasevalue;
					tag.setDouble(DataHelper.MULTIPLIER, multiplier);
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setDouble(DataHelper.OUTPUT, active ? ((TileEntityWindGenerator) te).subproduction : 0);
					return tag;
				}
			}

			if (te instanceof TileEntitySemifluidGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntitySemifluidGenerator) te).storage);
				tag.setDouble(DataHelper.CAPACITY, DataHelper.getShort(TileEntitySemifluidGenerator.class, "maxStorage", te));
				tag.setDouble(DataHelper.OUTPUT, DataHelper.getDouble(TileEntitySemifluidGenerator.class, "production", te));
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntitySemifluidGenerator) te).isConverting());
				FluidInfo.addTank(DataHelper.TANK, tag, ((TileEntitySemifluidGenerator) te).getFluidTank());
				return tag;
			}

			if (te instanceof TileEntityStirlingGenerator) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityStirlingGenerator) te).production > 0);
				tag.setDouble(DataHelper.ENERGY, ((TileEntityStirlingGenerator) te).EUstorage);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityStirlingGenerator) te).maxEUStorage);
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityStirlingGenerator) te).production);
				tag.setDouble(DataHelper.MULTIPLIER, ((TileEntityStirlingGenerator) te).productionpeerheat);
				return tag;
			}

			if (te instanceof TileEntityGeoGenerator) {
				tag.setDouble(DataHelper.ENERGY, ((TileEntityGeoGenerator) te).storage);
				tag.setDouble(DataHelper.CAPACITY, ((TileEntityGeoGenerator) te).maxStorage);
				Boolean active = ((TileEntityGeoGenerator) te).isConverting();
				tag.setBoolean(DataHelper.ACTIVE, active);
				tag.setDouble(DataHelper.OUTPUT, active ? ((TileEntityGeoGenerator) te).production : 0);
				return tag;
			}

			if (te instanceof TileEntityKineticGenerator) {
				Boolean active = ((TileEntityKineticGenerator)te).getActive();
				tag.setBoolean(DataHelper.ACTIVE, active);
				tag.setDouble(DataHelper.ENERGY, ((TileEntityKineticGenerator)te).EUstorage);
				tag.setDouble(DataHelper.CAPACITY, DataHelper.getInt(TileEntityKineticGenerator.class, "maxEUStorage", te));
				tag.setDouble(DataHelper.OUTPUT, ((TileEntityKineticGenerator) te).getproduction());
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getDouble(TileEntityKineticGenerator.class, "productionpeerkineticunit", te));
				return tag;
			}

			// KineticData
			if (te instanceof TileEntityElectricKineticGenerator) {
				TileEntityElectricKineticGenerator generator = (TileEntityElectricKineticGenerator) te;
				int counter = 0;
				for (int i = 0; i < generator.slotMotor.size(); i++)
					if (generator.slotMotor.get(i) != null)
						counter++;
				tag.setInteger("motors", counter);
				tag.setBoolean(DataHelper.ACTIVE, counter != 0);
				tag.setDouble(DataHelper.ENERGYKU, generator.ku);
				tag.setDouble(DataHelper.CAPACITYKU, generator.maxKU);
				ArrayList values = getHookValues(te);
				if (values != null)
					tag.setDouble(DataHelper.OUTPUTKU, (double) values.get(0));
				Energy energy = (Energy) generator.getComponent("energy");
				tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
				tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
				tag.setDouble(DataHelper.MULTIPLIER, DataHelper.getFloat(TileEntityElectricKineticGenerator.class, "kuPerEU", te));
				return tag;
			}
			if (te instanceof TileEntityManualKineticGenerator) {
				tag.setDouble(DataHelper.ENERGYKU, ((TileEntityManualKineticGenerator)te).currentKU);
				tag.setDouble(DataHelper.CAPACITYKU, ((TileEntityManualKineticGenerator)te).maxKU);
				return tag;
			}
			if (te instanceof TileEntitySteamKineticGenerator) {
				tag.setBoolean(DataHelper.ACTIVE, ((TileEntityBlock) te).getActive());
				tag.setDouble(DataHelper.OUTPUTHU, ((TileEntitySteamKineticGenerator) te).gethUoutput());
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
				tag.setInteger("height", generator.yCoord);
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
				tag.setDouble(DataHelper.MULTIPLIER, generator.getefficiency() * TileEntityWindKineticGenerator.outputModifier);
				tag.setInteger("height", generator.yCoord);
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
					Energy energy = (Energy) ((TileEntityElectricHeatGenerator) te).getComponent("energy");
					tag.setDouble(DataHelper.ENERGY, energy.getEnergy());
					tag.setDouble(DataHelper.CAPACITY, energy.getCapacity());
					int counter = 0;
					for (int i = 0; i < ((TileEntityElectricHeatGenerator) te).CoilSlot.size(); i++)
						if (((TileEntityElectricHeatGenerator) te).CoilSlot.get(i) != null)
							counter++;
					tag.setInteger("coils", counter);

					boolean active = ((TileEntityHeatSourceInventory)te).getActive();
					tag.setBoolean(DataHelper.ACTIVE, active);
					tag.setInteger(DataHelper.OUTPUTHU, active ? ((TileEntityHeatSourceInventory)te).gettransmitHeat() : 0);
					return tag;
				}
				if (te instanceof TileEntityFluidHeatGenerator) {
					boolean active = ((TileEntityFluidHeatGenerator)te).isConverting();
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
						if (((TileEntityRTHeatGenerator) te).fuelSlot.get(i) != null)
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
			if (te instanceof TileEntityReactorChamberElectric)
				return getReactorData(((TileEntityReactorChamberElectric) te).getReactor());
			if (te instanceof TileEntityNuclearReactorElectric)
				return getReactorData((TileEntityNuclearReactorElectric) te);
			if (te instanceof TileEntityReactorAccessHatch)
				return getReactorData(((TileEntityReactorChamberElectric)((TileEntityReactorAccessHatch) te).getReactor()).getReactor());
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
		if (stack == null)
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell || item instanceof ItemReactorMOX)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);
		return 0;
	}

	@Override
	public int getHeat(World world, int x, int y, int z) {
		IReactor reactor = IC2ReactorHelper.getReactorAround(world, x, y, z);
		if (reactor != null)
			return reactor.getHeat();
		reactor = IC2ReactorHelper.getReactor3x3(world, x, y, z);
		if (reactor != null)
			return reactor.getHeat();
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (!(te instanceof IFluidHandler))
			return null;

		FluidTankInfo[] list = ((IFluidHandler) te).getTankInfo(null);
		List<FluidInfo> result = new ArrayList<>();
		for (FluidTankInfo tank: list)
			result.add(new FluidInfo(tank.fluid, tank.capacity));

		return result;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = IC2Hooks.map.get(te);
		if (values == null)
			IC2Hooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems() {
		ModItems.blockAfsu = ModItems.register(new AFSU(), ItemAFSU.class, "afsu");
		ModItems.blockSeedAnalyzer = ModItems.register(new SeedAnalyzer(), "seed_analyzer");
		ModItems.blockSeedLibrary = ModItems.register(new SeedLibrary(), "seed_library");

		ModItems.itemThermometerDigital = ModItems.register(new ItemDigitalThermometer(), "thermometer_digital");
		ModItems.itemAFB = ModItems.register(new ItemAFB(), "afb");
		ModItems.itemAFSUUpgradeKit = ModItems.register(new ItemAFSUUpgradeKit(), "afsu_upgrade_kit");

		ItemKitMain.register(ItemKitIC2::new);
		ItemCardMain.register(ItemCardIC2::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.blockAverageCounter, new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'L', "plateLead" });

		Recipes.addShapedRecipe(ModItems.blockEnergyCounter, new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'I', "plateIron" });

		Recipes.addShapedRecipe(ModItems.blockAfsu, new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("iridiumPlate"),
				'G', IC2Items.getItem("glassFiberCableItem"),
				'M', IC2Items.getItem("mfsUnit"),
				'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE) });

		Recipes.addShapedRecipe(ModItems.blockSeedAnalyzer, new Object[] {
			" C ", "WAW", "WBW",
				'C', IC2Items.getItem("cropnalyzer"),
				'W', "plankWood",
				'A', new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING),
				'B', "circuitBasic" });

		Recipes.addShapedRecipe(ModItems.blockSeedLibrary, new Object[] {
			"GGG", "CAC", "CSC",
				'G', "blockGlass",
				'C', Blocks.chest,
				'A', "circuitAdvanced",
				'S', ModItems.blockSeedAnalyzer });

		Recipes.addShapedRecipe(ModItems.itemThermometerDigital, 32767, new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ModItems.itemThermometer,
				'I', "plateIron",
				'P', IC2Items.getItem("powerunitsmall") });

		Recipes.addShapedRecipe(ModItems.itemAFB, new Object[] {
				"GIG", "IUI", "GIG",
					'G', IC2Items.getItem("glassFiberCableItem"),
					'I', IC2Items.getItem("iridiumPlate"),
					'U', IC2Items.getItem("uuMatterCell") });

		Recipes.addShapedRecipe(ModItems.itemAFSUUpgradeKit, new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("iridiumPlate"),
				'G', IC2Items.getItem("glassFiberCableItem"),
				'M', IC2Items.getItem("mfsUnit"),
				'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE) });

		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_IC2,
			new Object[] { "CF", "PL", 'P', Items.paper, 'C', IC2Items.getItem("advIronIngot"),
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'L', "dyeLightGray" });

		Recipes.addKitRecipe(ItemCardType.KIT_IC2, ItemCardType.CARD_IC2);
	}
}
