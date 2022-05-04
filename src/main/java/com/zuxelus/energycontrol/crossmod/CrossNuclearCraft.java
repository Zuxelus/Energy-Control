package com.zuxelus.energycontrol.crossmod;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardNuclearCraft;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.items.kits.ItemKitNuclearCraft;
import com.zuxelus.energycontrol.recipes.Recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class CrossNuclearCraft extends CrossModBase {

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		/*if (te instanceof TileDecayGenerator) {
			reader.setInt("type", 1);
			reader.setInt("stored", ((TileDecayGenerator) te).getEnergyStored());
			reader.setInt("capacity", ((TileDecayGenerator) te).getMaxEnergyStored());
			reader.setInt("output", ((TileDecayGenerator) te).getGenerated());
			reader.setDouble("radiation", ((TileDecayGenerator) te).getRadiation());
			return CardState.OK;
		}
		if (te instanceof TileItemProcessor) {
			reader.setInt("type", 2);
			reader.setInt("stored", ((TileItemProcessor) te).getEnergyStored());
			reader.setInt("capacity", ((TileItemProcessor) te).getMaxEnergyStored());
			reader.setInt("power", ((TileItemProcessor) te).getProcessPower());
			reader.setDouble("speedM", ((TileItemProcessor) te).getSpeedMultiplier());
			reader.setDouble("powerM", ((TileItemProcessor) te).getPowerMultiplier());
			reader.setInt("time", ((TileItemProcessor) te).getProcessTime());
			return CardState.OK;
		}
		if (te instanceof TileFluidProcessor) {
			reader.setInt("type", 2);
			reader.setInt("stored", ((TileFluidProcessor) te).getEnergyStored());
			reader.setInt("capacity", ((TileFluidProcessor) te).getMaxEnergyStored());
			reader.setInt("power", ((TileFluidProcessor) te).getProcessPower());
			reader.setDouble("speedM", ((TileFluidProcessor) te).getSpeedMultiplier());
			reader.setDouble("powerM", ((TileFluidProcessor) te).getPowerMultiplier());
			reader.setInt("time", ((TileFluidProcessor) te).getProcessTime());
			return CardState.OK;
		}
		if (te instanceof TileSolarPanel) {
			reader.setInt("type", 4);
			reader.setInt("stored", ((TileSolarPanel) te).getEnergyStored(null));
			reader.setInt("capacity", ((TileSolarPanel) te).getMaxEnergyStored(null));
			//reader.setInt("output", ((TileSolarPanel) te).getGenerated());
			return CardState.OK;
		}
		if (te instanceof TileBattery) {
			reader.setInt("type", 5);
			reader.setInt("stored", ((TileBattery) te).getEnergyStored());
			reader.setInt("capacity", ((TileBattery) te).getMaxEnergyStored());
			return CardState.OK;
		}
		if (te instanceof TileFissionController) {
			reader.setInt("type", 6);
			TileFissionController reactor = (TileFissionController) te;
			reader.setBoolean("active", reactor.isProcessing);
			reader.setString("size", reactor.getLengthX() + "*" + reactor.getLengthY() + "*" + reactor.getLengthZ());
			reader.setString("fuel", reactor.getFuelName());
			reader.setInt("stored", reactor.getEnergyStored());
			reader.setInt("capacity", reactor.getMaxEnergyStored());
			reader.setDouble("efficiency", reactor.efficiency);
			reader.setDouble("heat", reactor.heat);
			reader.setInt("maxHeat", reactor.getMaxHeat());
			reader.setDouble("heatChange", reactor.heatChange);
			reader.setDouble("cooling", reactor.cooling);
			reader.setDouble("heatMult", reactor.heatMult);
			reader.setDouble("power", reactor.processPower);
			reader.setInt("cells", reactor.cells);
			return CardState.OK;
		}*/
		return null;
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
				'T', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'I', "dyeGreen" });

		Recipes.addKitRecipe(ItemCardType.KIT_NUCLEARCRAFT, ItemCardType.CARD_NUCLEARCRAFT);
	}
}
