package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardAdvGenerators;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEng;
import com.zuxelus.energycontrol.items.cards.ItemCardAppEngInv;
import com.zuxelus.energycontrol.items.kits.ItemKitAdvGenerators;
import com.zuxelus.energycontrol.items.kits.ItemKitAppEng;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.bdew.generators.config.ExchangerRecipeRegistry;
import net.bdew.generators.controllers.exchanger.TileExchangerController;
import net.bdew.generators.controllers.steam.TileSteamTurbineController;
import net.bdew.generators.controllers.syngas.TileSyngasController;
import net.bdew.generators.controllers.turbine.TileFuelTurbineController;
import net.bdew.generators.recipes.ExchangerRecipe;
import net.bdew.lib.resource.Resource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import scala.Option;

public class CrossAdvGenerators extends CrossModBase {
	public static Map<BlockEntity, ArrayList<Double>> map = new HashMap<BlockEntity, ArrayList<Double>>();

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		CompoundTag tag = new CompoundTag();
		if (te instanceof TileSteamTurbineController) {
			TileSteamTurbineController controller = (TileSteamTurbineController) te;
			tag.putBoolean(DataHelper.ACTIVE, controller.outputAverage().average() > 0);
			tag.putDouble(DataHelper.ENERGY, controller.powerOutput().getEnergyStored());
			tag.putDouble(DataHelper.CAPACITY, controller.powerOutput().getMaxEnergyStored());
			tag.putDouble(DataHelper.CONSUMPTIONMB, controller.steamAverage().average());
			tag.putDouble(DataHelper.OUTPUT, controller.outputAverage().average());
			tag.putDouble("speed", (Double) controller.speed().value());
			tag.putInt("turbines", (Integer) controller.numTurbines().value());
			FluidInfo.addTank(DataHelper.TANK, tag, controller.steam().getFluid(), controller.steam().getFluidAmount());
			return tag;
		}
		if (te instanceof TileFuelTurbineController) {
			TileFuelTurbineController controller = (TileFuelTurbineController) te;
			tag.putBoolean(DataHelper.ACTIVE, controller.outputAverage().average() > 0);
			tag.putDouble(DataHelper.ENERGY, controller.powerOutput().getEnergyStored());
			tag.putDouble(DataHelper.CAPACITY, controller.powerOutput().getMaxEnergyStored());
			tag.putDouble(DataHelper.CONSUMPTIONMB, controller.fuelPerTickAverage().average());
			tag.putDouble(DataHelper.OUTPUT, controller.outputAverage().average());
			tag.putInt("turbines", (Integer) controller.numTurbines().value());
			FluidInfo.addTank(DataHelper.TANK, tag, controller.fuel().getFluid(), controller.fuel().getFluidAmount());
			return tag;
		}
		if (te instanceof TileSyngasController) {
			TileSyngasController controller = (TileSyngasController) te;
			tag.putBoolean(DataHelper.ACTIVE, controller.avgSyngasProduced().average() > 0);
			tag.putDouble("carbonIn", controller.avgCarbonUsed().average());
			tag.putDouble(DataHelper.OUTPUTMB, controller.avgSyngasProduced().average());
			tag.putDouble("steamIn", controller.avgSyngasProduced().average() * (Double) controller.cfg().steamPerMBSyngas().apply());
			ArrayList<Double> values = getHookValues(te);
			if (values != null) {
				tag.putDouble("steamOut", values.get(0));
				tag.putDouble("waterIn", values.get(1));
			}
			tag.putDouble(DataHelper.HEAT, (Double) controller.heat().value());
			tag.putInt("heatingChambers", (Integer) controller.heatingChambers().value());
			tag.putInt("mixingChambers", (Integer) controller.mixingChambers().value());
			tag.putDouble("carbon", (Double) controller.carbonBuffer().value());
			FluidInfo.addTank(DataHelper.TANK, tag, controller.waterTank().getFluidInTank(0),controller.waterTank().getFluidInTank(0).getAmount());
			FluidInfo.addTank(DataHelper.TANK2, tag, controller.steamTank().getFluidInTank(0),controller.steamTank().getFluidInTank(0).getAmount());
			FluidInfo.addTank(DataHelper.TANK3, tag, controller.syngasTank().getFluidInTank(0),controller.syngasTank().getFluidInTank(0).getAmount());
			return tag;
		}
		if (te instanceof TileExchangerController) {
			TileExchangerController controller = (TileExchangerController) te;
			tag.putBoolean(DataHelper.ACTIVE, controller.outputRate().average() > 0);
			tag.putDouble(DataHelper.HEAT, (Double) controller.heat().value());
			tag.putDouble(DataHelper.CONSUMPTIONMB, controller.inputRate().average());
			tag.putDouble(DataHelper.OUTPUTMB, controller.outputRate().average());
			Option<Resource> coolerIn = controller.coolerIn().resource();
			if (!coolerIn.isEmpty()) {
				Resource water = coolerIn.get();
				tag.putString(DataHelper.TANK, String.format("%s: %.3f mB", water.kind().getName().getString(), water.amount()));
				Option<ExchangerRecipe> option = ExchangerRecipeRegistry.findCooler(water);
				if (!option.isEmpty()) {
					ExchangerRecipe recipe = option.get();
					tag.putDouble("waterIn", controller.outputRate().average() * recipe.inPerHU() / recipe.outPerHU());
				}
			}
			Option<Resource> coolerOut = controller.coolerOut().resource();
			if (!coolerOut.isEmpty())
				tag.putString(DataHelper.TANK2, String.format("%s: %.3f mB", coolerOut.get().kind().getName().getString(), coolerOut.get().amount()));
			Option<Resource> heaterIn = controller.heaterIn().resource();
			if (!heaterIn.isEmpty()) {
				Resource lava = heaterIn.get();
				tag.putString(DataHelper.TANK3, String.format("%s: %.3f mB", lava.kind().getName().getString(), lava.amount()));
				Option<ExchangerRecipe> option = ExchangerRecipeRegistry.findHeater(lava);
				if (!option.isEmpty()) {
					ExchangerRecipe recipe = option.get();
					tag.putDouble("obsidianOut", controller.inputRate().average() / recipe.inPerHU() * recipe.outPerHU() * 1000);
				}
			}
			Option<Resource> heaterOut = controller.heaterOut().resource();
			if (!heaterOut.isEmpty())
				tag.putString(DataHelper.TANK4, String.format("%s: %.3f mB", heaterOut.get().kind().getName().getString(), heaterOut.get().amount() * 1000));

			return tag;
		}
		return null;
	}

	public ArrayList<Double> getHookValues(BlockEntity te) {
		ArrayList<Double> values = map.get(te);
		if (values == null)
			map.put(te, null);
		return values;
	}

	@Override
	public void registerItems(RegisterHelper<Item> event) {
		ModItems.kit_adv_generators = new ItemKitAdvGenerators();
		event.register("kit_adv_generators", ModItems.kit_adv_generators);
		ModItems.card_adv_generators = new ItemCardAdvGenerators();
		event.register("card_adv_generators", ModItems.card_adv_generators);
	}
}
