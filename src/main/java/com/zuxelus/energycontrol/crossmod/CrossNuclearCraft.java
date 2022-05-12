package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.hooks.NuclearCraftHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardNuclearCraft;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.items.kits.ItemKitNuclearCraft;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import nc.tile.generator.*;
import nc.tile.machine.TileElectricFurnace;
import nc.tile.machine.TileMachineBase;
import nc.tile.storage.TileStorage;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

public class CrossNuclearCraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DataHelper.EUTYPE, "RF");
		if (te instanceof TileMachineBase) {
			TileMachineBase base = (TileMachineBase) te;
			tag.setDouble(DataHelper.ENERGY, base.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			return tag;
		}
		if (te instanceof TileElectricFurnace) {
			TileElectricFurnace base = (TileElectricFurnace) te;
			tag.setDouble(DataHelper.ENERGY, base.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			return tag;
		}
		if (te instanceof TileReactionGenerator) {
			TileReactionGenerator base = (TileReactionGenerator) te;
			tag.setDouble(DataHelper.ENERGY, base.energy);
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			return tag;
		}
		if (te instanceof TileSolarPanel) {
			TileSolarPanel panel = (TileSolarPanel) te;
			tag.setDouble(DataHelper.ENERGY, panel.storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, panel.storage.getMaxEnergyStored());
			return tag;
		}
		if (te instanceof TileStorage) {
			tag.setDouble(DataHelper.ENERGY, ((TileStorage) te).storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileStorage) te).storage.getMaxEnergyStored());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileFissionReactorSteam) {
			result.add(new FluidInfo(((TileFissionReactorSteam) te).tank));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileMachineBase) {
			TileMachineBase base = (TileMachineBase) te;
			tag.setDouble(DataHelper.ENERGY, base.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			Boolean active = base.cookTime > 0;
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.CONSUMPTION, active ? (int) Math.ceil(base.getEnergyRequired / base.getProcessTime) : 0);
			return tag;
		}
		if (te instanceof TileElectricFurnace) {
			TileElectricFurnace base = (TileElectricFurnace) te;
			tag.setDouble(DataHelper.ENERGY, base.getEnergy());
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			Boolean active = base.cookTime > 0;
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.CONSUMPTION, active ? (int) Math.ceil(base.getRequiredEnergy / base.getFurnaceSpeed) : 0);
			return tag;
		}
		if (te instanceof TileReactionGenerator) {
			TileReactionGenerator base = (TileReactionGenerator) te;
			tag.setDouble(DataHelper.ENERGY, base.energy);
			tag.setDouble(DataHelper.CAPACITY, base.getMaxEnergyStored(null));
			Boolean active = base.fuellevel >= base.requiredFuel && base.storage.getEnergyStored() <= base.storage.getMaxEnergyStored() - TileReactionGenerator.power && base.reactantlevel >= base.requiredReactant;
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.OUTPUT, active ? base.power : 0);
			tag.setInteger("fuel", base.fuellevel);
			return tag;
		}
		if (te instanceof TileSolarPanel) {
			TileSolarPanel panel = (TileSolarPanel) te;
			tag.setDouble(DataHelper.ENERGY, panel.storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, panel.storage.getMaxEnergyStored());
			Boolean active = panel.storage.getEnergyStored() == 0 && te.getWorldObj().canBlockSeeTheSky(te.xCoord, te.yCoord + 1, te.zCoord) && te.getWorldObj().getBlockLightValue(te.xCoord, te.yCoord + 1, te.zCoord) > 0; 
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.OUTPUT, active ? (int) Math.ceil(1D * panel.power * te.getWorldObj().getBlockLightValue(te.xCoord, te.yCoord + 1, te.zCoord) * te.getWorldObj().getBlockLightValue(te.xCoord, te.yCoord + 1, te.zCoord) / 225) : 0);
			return tag;
		}
		if (te instanceof TileStorage) {
			tag.setDouble(DataHelper.ENERGY, ((TileStorage) te).storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, ((TileStorage) te).storage.getMaxEnergyStored());
			ArrayList values = getHookValues(te);
			if (values != null)
				tag.setLong("diff", ((Long) values.get(0) - (Long) values.get(20)) / 20);
			return tag;
		}
		if (te instanceof TileFissionReactor) {
			TileFissionReactor reactor = (TileFissionReactor) te;
			tag.setDouble(DataHelper.ENERGY, reactor.storage.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, reactor.storage.getMaxEnergyStored());
			Boolean active = reactor.off == 0 && reactor.flag; 
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.OUTPUT, active ? reactor.EReal : 0);
			tag.setInteger(DataHelper.HEAT, reactor.heat);
			tag.setInteger(DataHelper.MAXHEAT, 1000000);
			tag.setInteger("heatChange", active ? reactor.HReal : reactor.off == 1 && !reactor.flag && reactor.heat > 0 ? reactor.HCooling : 0);
			tag.setDouble("fuelp", reactor.fueltime / 100000);
			tag.setString("size", (reactor.lx - 2) + "*" + (reactor.ly - 2) + "*" + (reactor.lz - 2));
			tag.setInteger("cells", reactor.numberOfCells);
			return tag;
		}
		if (te instanceof TileFissionReactorSteam) {
			TileFissionReactorSteam reactor = (TileFissionReactorSteam) te;
			Boolean active = reactor.off == 0 && reactor.flag; 
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.OUTPUTMB, active ? reactor.SReal : 0);
			tag.setInteger(DataHelper.HEAT, reactor.heat);
			tag.setInteger(DataHelper.MAXHEAT, 1000000);
			tag.setInteger("heatChange", active ? reactor.HReal : reactor.off == 1 && !reactor.flag && reactor.heat > 0 ? reactor.HCooling : 0);
			tag.setDouble("fuelp", reactor.fueltime / 100000);
			tag.setString("size", (reactor.lx - 2) + "*" + (reactor.ly - 2) + "*" + (reactor.lz - 2));
			tag.setInteger("cells", reactor.numberOfCells);
			FluidInfo.addTank(DataHelper.TANK, tag, reactor.tank);
			return tag;
		}
		if (te instanceof TileFusionReactorBlock) {
			TileEntity core = te.getWorldObj().getTileEntity(te.xCoord + ((TileFusionReactorBlock) te).xOffset, te.yCoord + ((TileFusionReactorBlock) te).yOffset, te.zCoord + ((TileFusionReactorBlock) te).zOffset);
			if (core instanceof TileFusionReactor) {
				TileFusionReactor reactor = (TileFusionReactor) core;
				tag.setBoolean(DataHelper.ACTIVE, reactor.EShown > 0);
				tag.setDouble(DataHelper.ENERGY, reactor.storage.getEnergyStored());
				tag.setDouble(DataHelper.CAPACITY, reactor.storage.getMaxEnergyStored());
				tag.setDouble(DataHelper.OUTPUT, reactor.EShown);
				tag.setInteger(DataHelper.HEAT, (int) (reactor.heat * 1000000));
				tag.setDouble("efficiency", reactor.efficiency);
				tag.setString("level1", f1(reactor) + " " + StatCollector.translateToLocal("gui.level1") + ": " + Math.round((level1(reactor) / 64000)) + "%");
				tag.setString("level2", f2(reactor) + " " + StatCollector.translateToLocal("gui.level1") + ": " + Math.round((level2(reactor) / 64000)) + "%");
				tag.setDouble("HOut", reactor.HOut / 100);
				tag.setDouble("DOut", reactor.DOut / 100);
				tag.setDouble("TOut", reactor.TOut / 100);
				tag.setDouble("HE3Out", reactor.HE3Out / 100);
				tag.setDouble("HE4Out", reactor.HE4Out / 100);
				tag.setDouble("nOut", reactor.nOut / 100);
				return tag;
			}
		}
		if (te instanceof TileFusionReactorSteamBlock) {
			TileEntity core = te.getWorldObj().getTileEntity(te.xCoord + ((TileFusionReactorSteamBlock) te).xOffset, te.yCoord + ((TileFusionReactorSteamBlock) te).yOffset, te.zCoord + ((TileFusionReactorSteamBlock) te).zOffset);
			if (core instanceof TileFusionReactorSteam) {
				TileFusionReactorSteam reactor = (TileFusionReactorSteam) core;
				tag.setBoolean(DataHelper.ACTIVE, reactor.SShown > 0);
				tag.setDouble(DataHelper.ENERGY, reactor.storage.getEnergyStored());
				tag.setDouble(DataHelper.CAPACITY, reactor.storage.getMaxEnergyStored());
				tag.setDouble(DataHelper.OUTPUTMB, reactor.SShown);
				tag.setInteger(DataHelper.HEAT, (int) (reactor.heat * 1000000));
				tag.setDouble("efficiency", reactor.efficiency);
				tag.setString("level1", f1(reactor) + " " + StatCollector.translateToLocal("gui.level1") + ": " + Math.round((level1(reactor) / 64000)) + "%");
				tag.setString("level2", f2(reactor) + " " + StatCollector.translateToLocal("gui.level1") + ": " + Math.round((level2(reactor) / 64000)) + "%");
				tag.setDouble("HOut", reactor.HOut / 100);
				tag.setDouble("DOut", reactor.DOut / 100);
				tag.setDouble("TOut", reactor.TOut / 100);
				tag.setDouble("HE3Out", reactor.HE3Out / 100);
				tag.setDouble("HE4Out", reactor.HE4Out / 100);
				tag.setDouble("nOut", reactor.nOut / 100);
				return tag;
			}
		}
		return null;
	}

	public static String f1(TileFusionReactor reactor) {
		if (reactor.HLevel > 0)
			return "Hydrogen";
		if (reactor.DLevel > 0)
			return "Deuterium";
		if (reactor.TLevel > 0)
			return "Tritium";
		if (reactor.HeLevel > 0)
			return "Helium-3";
		if (reactor.BLevel > 0)
			return "Boron-11";
		if (reactor.Li6Level > 0)
			return "Lithium-6";
		if (reactor.Li7Level > 0)
			return "Lithium-7";
		return "Fuel";
	}

	public static String f2(TileFusionReactor reactor) {
		if (reactor.HLevel2 > 0)
			return "Hydrogen";
		if (reactor.DLevel2 > 0)
			return "Deuterium";
		if (reactor.TLevel2 > 0)
			return "Tritium";
		if (reactor.HeLevel2 > 0)
			return "Helium-3";
		if (reactor.BLevel2 > 0)
			return "Boron-11";
		if (reactor.Li6Level2 > 0)
			return "Lithium-6";
		if (reactor.Li7Level2 > 0)
			return "Lithium-7";
		return "Fuel";
	}

	public static int level1(TileFusionReactor reactor) {
		if (reactor.HLevel > 0)
			return reactor.HLevel;
		if (reactor.DLevel > 0)
			return reactor.DLevel;
		if (reactor.TLevel > 0)
			return reactor.TLevel;
		if (reactor.HeLevel > 0)
			return reactor.HeLevel;
		if (reactor.BLevel > 0)
			return reactor.BLevel;
		if (reactor.Li6Level > 0)
			return reactor.Li6Level;
		if (reactor.Li7Level > 0)
			return reactor.Li7Level;
		return 0;
	}

	public static int level2(TileFusionReactor reactor) {
		if (reactor.HLevel2 > 0)
			return reactor.HLevel2;
		if (reactor.DLevel2 > 0)
			return reactor.DLevel2;
		if (reactor.TLevel2 > 0)
			return reactor.TLevel2;
		if (reactor.HeLevel2 > 0)
			return reactor.HeLevel2;
		if (reactor.BLevel2 > 0)
			return reactor.BLevel2;
		if (reactor.Li6Level2 > 0)
			return reactor.Li6Level2;
		if (reactor.Li7Level2 > 0)
			return reactor.Li7Level2;
		return 0;
	}

	public static String f1(TileFusionReactorSteam reactor) {
		if (reactor.HLevel > 0)
			return "Hydrogen";
		if (reactor.DLevel > 0)
			return "Deuterium";
		if (reactor.TLevel > 0)
			return "Tritium";
		if (reactor.HeLevel > 0)
			return "Helium-3";
		if (reactor.BLevel > 0)
			return "Boron-11";
		if (reactor.Li6Level > 0)
			return "Lithium-6";
		if (reactor.Li7Level > 0)
			return "Lithium-7";
		return "Fuel";
	}

	public static String f2(TileFusionReactorSteam reactor) {
		if (reactor.HLevel2 > 0)
			return "Hydrogen";
		if (reactor.DLevel2 > 0)
			return "Deuterium";
		if (reactor.TLevel2 > 0)
			return "Tritium";
		if (reactor.HeLevel2 > 0)
			return "Helium-3";
		if (reactor.BLevel2 > 0)
			return "Boron-11";
		if (reactor.Li6Level2 > 0)
			return "Lithium-6";
		if (reactor.Li7Level2 > 0)
			return "Lithium-7";
		return "Fuel";
	}

	public static int level1(TileFusionReactorSteam reactor) {
		if (reactor.HLevel > 0)
			return reactor.HLevel;
		if (reactor.DLevel > 0)
			return reactor.DLevel;
		if (reactor.TLevel > 0)
			return reactor.TLevel;
		if (reactor.HeLevel > 0)
			return reactor.HeLevel;
		if (reactor.BLevel > 0)
			return reactor.BLevel;
		if (reactor.Li6Level > 0)
			return reactor.Li6Level;
		if (reactor.Li7Level > 0)
			return reactor.Li7Level;
		return 0;
	}

	public static int level2(TileFusionReactorSteam reactor) {
		if (reactor.HLevel2 > 0)
			return reactor.HLevel2;
		if (reactor.DLevel2 > 0)
			return reactor.DLevel2;
		if (reactor.TLevel2 > 0)
			return reactor.TLevel2;
		if (reactor.HeLevel2 > 0)
			return reactor.HeLevel2;
		if (reactor.BLevel2 > 0)
			return reactor.BLevel2;
		if (reactor.Li6Level2 > 0)
			return reactor.Li6Level2;
		if (reactor.Li7Level2 > 0)
			return reactor.Li7Level2;
		return 0;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = NuclearCraftHooks.map.get(te);
		if (values == null)
			NuclearCraftHooks.map.put(te, null);
		return values;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitNuclearCraft::new);
		ItemCardMain.register(ItemCardNuclearCraft::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_NUCLEARCRAFT,
			new Object[] { "IT", "PD", 'P', Items.paper, 'D', "dyeGreen",
				'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', "ingotCopper" });

		Recipes.addKitRecipe(ItemCardType.KIT_NUCLEARCRAFT, ItemCardType.CARD_NUCLEARCRAFT);
	}
}
