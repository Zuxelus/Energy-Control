package com.zuxelus.energycontrol.crossmod;

import static gregapi.data.CS.F;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.hooks.GregTechHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardGregTech;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitGregTech;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import gregapi.data.TD.Energy;
import gregapi.item.IItemEnergy;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.tileentity.base.TileEntityBase05Inventories;
import gregapi.tileentity.connectors.MultiTileEntityAxle;
import gregapi.tileentity.connectors.MultiTileEntityPipeFluid;
import gregapi.tileentity.connectors.MultiTileEntityWireElectric;
import gregapi.tileentity.machines.MultiTileEntityBasicMachineElectric;
import gregapi.util.ST;
import gregapi.util.UT;
import gregtech.tileentity.energy.converters.MultiTileEntityBoilerTank;
import gregtech.tileentity.energy.converters.MultiTileEntityDynamoElectric;
import gregtech.tileentity.energy.converters.MultiTileEntityMotorElectric;
import gregtech.tileentity.energy.converters.MultiTileEntityTurbineSteam;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorGas;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorMetal;
import gregtech.tileentity.energy.generators.MultiTileEntityGeneratorSolid;
import gregtech.tileentity.energy.generators.MultiTileEntityMotorLiquid;
import gregtech.tileentity.energy.generators.MultiTileEntitySolarPanelElectric;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorCore2x2;
import gregtech.tileentity.energy.reactors.MultiTileEntityReactorRodNuclear;
import gregtech.tileentity.energy.storage.MultiTileEntityBatteryBox;
import gregtech.tileentity.energy.transformers.MultiTileEntityTransformerElectric;
import gregtech.tileentity.tanks.MultiTileEntityBarrelMetal;
import gregtech.tileentity.tools.MultiTileEntityMold;
import gregtech.tileentity.tools.MultiTileEntitySmeltery;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

public class CrossGregTech6 extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof MultiTileEntityBatteryBox) {
			MultiTileEntityBatteryBox box = (MultiTileEntityBatteryBox) te;
			long energy = 0;
			tag.setDouble(DataHelper.ENERGY, box.mEnergy);
			for (ItemStack tStack : box.getInventory())
				if (ST.valid(tStack))
					if (tStack.getItem() instanceof IItemEnergy) {
						energy = energy + ((IItemEnergy) tStack.getItem()).getEnergyStored(box.mEnergyType, tStack);
					}
			tag.setDouble("battery", energy);
			tag.setDouble("limit0", ((MultiTileEntityBatteryBox) te).mInput * 2 * 40 * ((MultiTileEntityBatteryBox) te).invsize());
			tag.setDouble("limit1", ((MultiTileEntityBatteryBox) te).mInput * 6 * 40 * ((MultiTileEntityBatteryBox) te).invsize());
			tag.setDouble(DataHelper.CAPACITY, ((MultiTileEntityBatteryBox) te).mInput * 8 * 40 * ((MultiTileEntityBatteryBox) te).invsize());
			addHookData(te, tag, DataHelper.CONSUMPTION, DataHelper.OUTPUT, true);
			return tag;
		}
		if (te instanceof MultiTileEntityGeneratorMetal) {
			boolean active = DataHelper.getBoolean(MultiTileEntityGeneratorSolid.class, "mBurning", te);
			tag.setBoolean(DataHelper.ACTIVE, active);
			tag.setDouble(DataHelper.ENERGYHU, DataHelper.getLong(MultiTileEntityGeneratorSolid.class, "mEnergy", te));
			tag.setDouble(DataHelper.OUTPUTHU, active ? DataHelper.getLong(MultiTileEntityGeneratorSolid.class, "mRate", te) : 0);
			ItemStack[] list = ((MultiTileEntityGeneratorMetal) te).getInventory();
			if (list[0] != null)
				tag.setTag("slot0", list[0].writeToNBT(new NBTTagCompound()));
			if (list[1] != null)
				tag.setTag("slot1", list[1].writeToNBT(new NBTTagCompound()));
			return tag;
		}
		if (te instanceof MultiTileEntityGeneratorGas) {
			FluidTankInfo[] info = ((MultiTileEntityGeneratorGas) te).getTankInfo(null);
			if (info.length > 0) {
				FluidInfo.addTank(DataHelper.TANK, tag, info[0].fluid, "L");
				tag.setDouble(DataHelper.CAPACITYL, info[0].capacity);
			}
			return tag;
		}
		if (te instanceof MultiTileEntitySmeltery) {
			try {
				Field field = MultiTileEntitySmeltery.class.getDeclaredField("mContent");
				field.setAccessible(true);
				List<OreDictMaterialStack> list = (List<OreDictMaterialStack>) field.get(te);
				StringBuilder builder = new StringBuilder();
				for (OreDictMaterialStack item : list) {
					if (builder.length() > 0)
						builder.append(",");
					builder.append(String.format("%s: %s", item.mMaterial.mNameLocal, PanelString.getFormatter().format(item.mAmount / 648648000d)));
				}
				tag.setString("content", builder.toString());
			} catch (Throwable t) { }
			tag.setDouble(DataHelper.HEAT, ((MultiTileEntitySmeltery) te).getTemperatureValue((byte) 0));
			return tag;
		}
		if (te instanceof MultiTileEntityBarrelMetal) {
			FluidInfo.addTank(DataHelper.TANK, tag, ((MultiTileEntityBarrelMetal) te).mTank.get(), "L");
			tag.setDouble(DataHelper.CAPACITYL, ((MultiTileEntityBarrelMetal) te).mTank.capacity());
			return tag;
		}
		if (te instanceof MultiTileEntityMold) {
			tag.setDouble(DataHelper.HEAT, ((MultiTileEntityMold) te).getTemperatureValue((byte) 0));
			return tag;
		}
		if (te instanceof MultiTileEntityPipeFluid) {
			FluidInfo.addTank(DataHelper.TANK, tag, ((MultiTileEntityPipeFluid) te).mTanks[0].get(), "L");
			tag.setDouble(DataHelper.CAPACITYL, ((MultiTileEntityPipeFluid) te).mTanks[0].capacity());
			ArrayList<Long> values = getHookValues(te);
			if (values != null) {
				tag.setDouble(DataHelper.OUTPUTL, getAverage(values, 0, GregTechHooks.len));
			}
			return tag;
		}
		if (te instanceof MultiTileEntityAxle) {
			ArrayList<Long> values = getHookValues(te);
			if (values != null) {
				long energy = 0;
				for (int i = 0; i < 20; i++)
					energy = energy + values.get(i);
				tag.setDouble(DataHelper.CONSUMPTIONRU, energy / 20.0d);
			}
			tag.setDouble(DataHelper.OUTPUTRU, ((MultiTileEntityAxle) te).mTransferredLast);
			return tag;
		}
		if (te instanceof MultiTileEntityBoilerTank) {
			FluidTankInfo[] info = ((MultiTileEntityBoilerTank) te).getTankInfo(null);
			if (info.length > 0) {
				FluidInfo.addTank(DataHelper.TANK, tag, info[0].fluid, "L");
				FluidInfo.addTank(DataHelper.TANK2, tag, info[1].fluid, "L");
			}
			ArrayList<Long> values = getHookValues(te);
			if (values != null) {
				long water = 0;
				long steam = 0;
				long energy = 0;
				for (int i = 0; i < 60; i++) {
					if (i < 20)
						water = water + values.get(i);
					if (i > 19 && i < 40)
						steam = steam + values.get(i);
					if (i > 39)
						energy = energy + values.get(i);
				}
				tag.setDouble(DataHelper.CONSUMPTIONL, water / 20.0d);
				tag.setDouble(DataHelper.OUTPUTST, steam / 20.0d);
				tag.setDouble(DataHelper.CONSUMPTIONHU, energy / 20.0d);
			}
			return tag;
		}
		if (te instanceof MultiTileEntityTurbineSteam) {
			FluidTankInfo[] info = ((MultiTileEntityTurbineSteam) te).getTankInfo(null);
			FluidInfo.addTank(DataHelper.TANK, tag, info[0].fluid, "L");
			ArrayList<Long> values = getHookValues(te);
			/*if (values != null) {
				double energy = 0;
				double water = 0;
				for (int i = 0; i < GregTechHooks.len * 2; i++) {
					if (i < GregTechHooks.len)
						energy = energy + values.get(i);
					if (i > GregTechHooks.len - 1)
						water = water + values.get(i);
				}
				tag.setDouble(DataHelper.CONSUMPTIONST, energy / GregTechHooks.len * 4);
				tag.setDouble(DataHelper.OUTPUTRU, energy / GregTechHooks.len * 2 / 3);
				tag.setDouble(DataHelper.OUTPUTL, water / GregTechHooks.len);
			}*/
			tag.setDouble(DataHelper.CONSUMPTIONST, getAverage(values, 0, GregTechHooks.len));
			tag.setDouble(DataHelper.OUTPUTRU, getAverage(values, GregTechHooks.len, GregTechHooks.len));
			tag.setDouble(DataHelper.OUTPUTL, getAverage(values, GregTechHooks.len * 2, 50));
			return tag;
		}
		if (te instanceof MultiTileEntityTransformerElectric) {
			tag.setDouble(DataHelper.ENERGY, ((MultiTileEntityTransformerElectric) te).mStorage.mEnergy);
			addHookData(te, tag, DataHelper.CONSUMPTION, DataHelper.OUTPUT, true);
			return tag;
		}
		if (te instanceof MultiTileEntityDynamoElectric) {
			tag.setDouble(DataHelper.ENERGY, ((MultiTileEntityDynamoElectric) te).mStorage.mEnergy);
			addHookData(te, tag, DataHelper.CONSUMPTIONRU, DataHelper.OUTPUT, true);
			return tag;
		}
		if (te instanceof MultiTileEntityMotorElectric) {
			tag.setDouble(DataHelper.ENERGYRU, ((MultiTileEntityMotorElectric) te).mStorage.mEnergy);
			addHookData(te, tag, DataHelper.CONSUMPTION, DataHelper.OUTPUTRU, true);
			return tag;
		}
		if (te instanceof MultiTileEntityWireElectric) {
			ArrayList<Long> values = getHookValues(te);
			if (values != null) {
				tag.setDouble(DataHelper.OUTPUT, getAverage(values, 0, GregTechHooks.len));
				tag.setDouble(DataHelper.ENERGY, values.get(0));
				tag.setDouble("amperage", values.get(GregTechHooks.len));
			}
			return tag;
		}
		if (te instanceof MultiTileEntitySolarPanelElectric) {
			addHookData(te, tag, null, DataHelper.OUTPUT, false);
			return tag;
		}
		if (te instanceof MultiTileEntityMotorLiquid) {
			FluidTankInfo[] info = ((MultiTileEntityMotorLiquid) te).getTankInfo(null);
			if (info.length > 0) {
				FluidInfo.addTank(DataHelper.TANK, tag, info[0].fluid, "L");
				FluidInfo.addTank(DataHelper.TANK2, tag, info[1].fluid, "L");
			}
			addHookData(te, tag, DataHelper.OUTPUTRU, DataHelper.CONSUMPTIONL, false);
			return tag;
		}
		if (te instanceof MultiTileEntityBasicMachineElectric) {
			IFluidTank[] list = ((MultiTileEntityBasicMachineElectric) te).mTanksInput;
			int j = 2;
			for (int i = 0; i < list.length; i++)
				if (list[i] != null) {
					FluidInfo.addTank(String.format("%s%s", DataHelper.TANK, j), tag, list[i].getFluid(), "L");
					j++;
				}
			list = ((MultiTileEntityBasicMachineElectric) te).mTanksOutput;
			for (int i = 0; i < list.length; i++)
				if (list[i] != null) {
					FluidInfo.addTank(String.format("%s%s", DataHelper.TANK, j), tag, list[i].getFluid(), "L");
					j++;
				}
			addHookData(te, tag, DataHelper.CONSUMPTION, null, true);
			return tag;
		}
		if (te instanceof MultiTileEntityReactorCore2x2) {
			IFluidTank[] tanks = ((MultiTileEntityReactorCore2x2) te).mTanks;
			FluidInfo.addTank(DataHelper.TANK, tag, tanks[0].getFluid(), "L");
			FluidInfo.addTank(DataHelper.TANK2, tag, tanks[1].getFluid(), "L");
			ArrayList<Long> values = getHookValues(te);
			if (values != null) {
				tag.setDouble(DataHelper.OUTPUTL, getAverage(values, 0, GregTechHooks.len));
				tag.setDouble(DataHelper.CONSUMPTIONL, getAverage(values, GregTechHooks.len, GregTechHooks.len));
			}
			long dur = Long.MAX_VALUE;
			for (ItemStack stack: DataHelper.getItemStackList(TileEntityBase05Inventories.class, "mInventory", te)) {
				if (stack != null && stack.hasTagCompound()) {
					NBTTagCompound nbt = stack.getTagCompound();
					long value = nbt.getLong("gt.durability");
					if (value > 0 && value < dur)
						dur = value;
				}
			}
			if (dur == Long.MAX_VALUE)
				dur = 0;
			tag.setDouble("remaining", dur / 120000.0d);
			tag.setString("neutrons", Arrays.toString(((MultiTileEntityReactorCore2x2) te).mNeutronCounts));
			return tag;
		}
		return null;
	}

	@Override
	public ArrayList getHookValues(TileEntity te) {
		ArrayList values = GregTechHooks.map.get(te);
		if (values == null)
			GregTechHooks.map.put(te, null);
		return values;
	}

	public static double getAverage(ArrayList<Long> values, int start, int size) {
		if (values == null)
			return 0;
		double sum = 0;
		for (int i = start; i < start + size; i++)
			sum = sum + values.get(i);
		return size > 0 ? sum / size : 0;
	}

	public void addHookData(TileEntity te, NBTTagCompound tag, String consumption, String output, boolean showPackage) {
		ArrayList<Long> values = getHookValues(te);
		if (consumption != null) {
			double avg = getAverage(values, 0, GregTechHooks.len);
			if (showPackage) {
				if (GregTechHooks.buffer.containsKey(te)) {
					long[] list = GregTechHooks.buffer.get(te);
					if (avg == 0)
						list[2] = 0;
					tag.setDouble("packet", list[2]);
				}
			}
			tag.setDouble(consumption, avg);
		}
		if (output != null)
			tag.setDouble(output, getAverage(values, GregTechHooks.len, GregTechHooks.len));
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitGregTech::new);
		ItemCardMain.register(ItemCardGregTech::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GREGTECH,
				new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dyeGray",
					'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotBronze" });

		Recipes.addKitRecipe(ItemCardType.KIT_GREGTECH, ItemCardType.CARD_GREGTECH);
	}
}
