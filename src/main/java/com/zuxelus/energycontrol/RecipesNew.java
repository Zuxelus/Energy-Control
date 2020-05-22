package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ic2.IC2Cross;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class RecipesNew {

	public static void addRecipes() {
		if (CrossModLoader.ic2.getType() == IC2Cross.IC2Type.EXP)
			RecipesNew.addExpRecipes();
		else
			RecipesNew.addClassicRecipes();
	}

	private static void addExpRecipes() {
		ItemStack lampWhite = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_WHITE_OFF);
		Recipes.advRecipes.addRecipe(lampWhite, new Object[] {
			"GGG", "GWG", "GLG",
				'G', "paneGlass",
				'W', "dyeWhite",
				'L', Blocks.REDSTONE_LAMP});

		ItemStack lampOrange = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
		Recipes.advRecipes.addRecipe(lampOrange, new Object[] {
			"GGG", "GWG", "GLG",
				'G', "paneGlass",
				'W', "dyeOrange",
				'L', Blocks.REDSTONE_LAMP});

		ItemStack howler = new ItemStack(ItemHelper.howlerAlarm);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "plateIron",
				'M', IC2Items.getItem("crafting#electric_motor"),
				'N', Blocks.NOTEBLOCK,
				'C', "circuitBasic"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.industrialAlarm), new Object[] {
			"BBB", "BLB", "BHB",
				'B', "plateBronze",
				'L', lampOrange,
				'H', howler });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.thermalMonitor), new Object[] {
			"LLL", "LCL", "LRL", 
				'L', "plateLead", 
				'R', "dustRedstone", 
				'C', "circuitAdvanced"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.remoteThermo), new Object[] {
			"IFI", "IMI", "ITI",
				'T', new ItemStack(ItemHelper.thermalMonitor),
				'M', IC2Items.getItem("resource#machine"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'I', "plateIron" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.infoPanel), new Object[] {
			"PPP", "CMC", "IRI",
				'P', "paneGlassLime",
				'C', "circuitBasic",
				'I', "dyeBlack",
				'R', "dustRedstone",
				'M', IC2Items.getItem("resource#machine")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.infoPanelExtender), new Object[] {
			"PPP", "WRW", "WWW",
				'P', "paneGlassLime",
				'R', "dustRedstone",
				'W', "plankWood" });

		ItemStack infoPanelAdvanced = new ItemStack(ItemHelper.infoPanelAdvanced);
		Recipes.advRecipes.addRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC",
				'P', "paneGlassLime",
				'I', new ItemStack(ItemHelper.infoPanel),
				'1', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced",
				'C', IC2Items.getItem("crafting","carbon_plate")});

		ItemStack infoPanelExtenderAdvanced = new ItemStack(ItemHelper.infoPanelAdvancedExtender);
		Recipes.advRecipes.addRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CMC",
				'P', "paneGlassLime",
				'M', IC2Items.getItem("resource","machine"),
				'E', new ItemStack(ItemHelper.infoPanelExtender),
				'C', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.rangeTrigger), new Object[] {
			"PFP", "AMA", " R ",
				'P', "plateIron",
				'F', IC2Items.getItem("frequency_transmitter"),
				'A', "circuitAdvanced",
				'M', IC2Items.getItem("resource#machine"),
				'R', "dustRedstone"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.averageCounter), new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'L', "plateLead"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.energyCounter), new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'I', "plateIron"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.kitAssembler), new Object[] {
			" T ", "CMC", "FAA",
				'T', Blocks.CRAFTING_TABLE,
				'M', IC2Items.getItem("resource#machine"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'C', "circuitBasic",
				'A', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometer), new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "plateIron",
				'W', IC2Items.getItem("fluid_cell#water")});

		ItemStack digitalThermometer = new ItemStack(ItemHelper.itemThermometerDigital, 1, 32767);
		Recipes.advRecipes.addRecipe(digitalThermometer, new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ItemHelper.itemThermometer,
				'I', "plateIron",
				'P', IC2Items.getItem("crafting#small_power_unit")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE), new Object[] {
			"CCC", "IFI",
				'I', IC2Items.getItem("cable","type:copper,insulation:1"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'C', IC2Items.getItem("heat_storage")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR), new Object[] {
			"RYG", "WCM", "IAB",
				'R', "dyeRed",
				'Y', "dyeYellow",
				'G', "dyeGreen",
				'W', "dyeWhite",
				'C', "circuitBasic",
				'M', "dyeMagenta",
				'I', "dyeBlack",
				'A', "dyeCyan",
				'B', "dyeBlue"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("cable","type:tin,insulation:1"),
				'C', IC2Items.getItem("frequency_transmitter"),
				'M', new ItemStack(ItemHelper.infoPanelExtender),
				'R', ItemHelper.itemUpgrade,
				'P', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit), new Object[] { 
			"RF", "PO",
				'P', Items.PAPER,
				'R', "dustRedstone",
				'F', IC2Items.getItem("frequency_transmitter"),
				'O', "dyeRed"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_COUNTER), new Object[] {
			"CF", "PR",
				'P', Items.PAPER,
				'C', "circuitBasic",
				'F', IC2Items.getItem("frequency_transmitter"),
				'R', "dyeOrange" });

		ItemStack kitLiquid = new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID);
		Recipes.advRecipes.addRecipe(kitLiquid, new Object[] {
			"CF", "PB",
				'P', Items.PAPER,
				'C', Items.BUCKET,
				'F', IC2Items.getItem("frequency_transmitter"),
				'B', "dyeBlue" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID_ADVANCED), new Object[] {
			"BKB",
				'B', Items.BUCKET,
				'K', kitLiquid });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_GENERATOR), new Object[] {
			"CF", "PL",
				'P', Items.PAPER,
				'C', IC2Items.getItem("upgrade","energy_storage"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'L', "dyeLightGray"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_REACTOR), new Object[] {
			"DF", "PW", 
				'P', Items.PAPER,
				'D', digitalThermometer,
				'F', IC2Items.getItem("frequency_transmitter"),
				'W', "dyeYellow" });

		if (CrossModLoader.draconicEvolution.modLoaded)
			Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_DRACONIC), new Object[] {
				"RF", "PB", 
					'P', Items.PAPER,
					'R', "dustRedstone",
					'F', IC2Items.getItem("frequency_transmitter"),
					'B', "ingotDraconium" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TEXT), new Object[] {
			" C ", "PFP", " C ",
				'P', Items.PAPER,
				'C', "circuitBasic",
				'F', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TIME), new Object[]{
			" C ", "PWP", " C ",
				'C', Items.CLOCK,
				'P', Items.PAPER,
				'W', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCardHolder), new Object[] {
			" L ", "LCL", " L ",
				'C', Blocks.CHEST,
				'L', "leather"});

		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				Recipes.advRecipes.addShapelessRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i));

		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
	}

	private static void addClassicRecipes() {
		ItemStack lampWhite = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_WHITE_OFF);
		Recipes.advRecipes.addRecipe(lampWhite, new Object[] {
			"GGG", "GWG", "GLG",
				'G', "paneGlass",
				'W', "dyeWhite",
				'L', Blocks.REDSTONE_LAMP});

		ItemStack lampOrange = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
		Recipes.advRecipes.addRecipe(lampOrange, new Object[] {
			"GGG", "GWG", "GLG",
				'G', "paneGlass",
				'W', "dyeOrange",
				'L', Blocks.REDSTONE_LAMP});

		ItemStack howler = new ItemStack(ItemHelper.howlerAlarm);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "ingotIron",
				'M', "ingotIron",
				'N', Blocks.NOTEBLOCK,
				'C', "circuitBasic"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.industrialAlarm), new Object[] {
			"BBB", "BLB", "BHB",
				'B', "ingotBronze",
				'L', lampOrange,
				'H', howler });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.thermalMonitor), new Object[] {
			"LLL", "LCL", "LRL", 
				'L', "ingotGold", 
				'R', "dustRedstone", 
				'C', "circuitAdvanced"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.remoteThermo), new Object[] {
			"IFI", "IMI", "ITI",
				'T', new ItemStack(ItemHelper.thermalMonitor),
				'M', IC2Items.getItem("resource","machine"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'I', "ingotIron" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.infoPanel), new Object[] {
			"PPP", "CMC", "IRI",
				'P', "paneGlassLime",
				'C', "circuitBasic",
				'I', "dyeBlack",
				'R', "dustRedstone",
				'M', IC2Items.getItem("resource","machine")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.infoPanelExtender), new Object[] {
			"PPP", "WRW", "WWW",
				'P', "paneGlassLime",
				'R', "dustRedstone",
				'W', "plankWood" });

		ItemStack infoPanelAdvanced = new ItemStack(ItemHelper.infoPanelAdvanced);
		Recipes.advRecipes.addRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC",
				'P', "paneGlassLime",
				'I', new ItemStack(ItemHelper.infoPanel),
				'1', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced",
				'C', IC2Items.getItem("crafting","carbon_plate")});

		ItemStack infoPanelExtenderAdvanced = new ItemStack(ItemHelper.infoPanelAdvancedExtender);
		Recipes.advRecipes.addRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CMC",
				'P', "paneGlassLime",
				'M', IC2Items.getItem("resource","machine"),
				'E', new ItemStack(ItemHelper.infoPanelExtender),
				'C', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.rangeTrigger), new Object[] {
			"PFP", "AMA", " R ",
				'P', "ingotIron",
				'F', IC2Items.getItem("frequency_transmitter"),
				'A', "circuitAdvanced",
				'M', IC2Items.getItem("resource","machine"),
				'R', "dustRedstone"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.averageCounter), new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'L', "ingotGold"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.energyCounter), new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'I', "ingotIron"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.kitAssembler), new Object[] {
			" T ", "CMC", "FAA",
				'T', Blocks.CRAFTING_TABLE,
				'M', IC2Items.getItem("resource","machine"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'C', "circuitBasic",
				'A', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometer), new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "ingotIron",
				'W', IC2Items.getItem("cell","water")});

		ItemStack digitalThermometer = new ItemStack(ItemHelper.itemThermometerDigital, 1, 32767);
		Recipes.advRecipes.addRecipe(digitalThermometer, new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ItemHelper.itemThermometer,
				'I', "ingotIron",
				'P', "circuitBasic"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE), new Object[] {
			"CCC", "IFI",
				'I', IC2Items.getItem("cable","type:copper,insulation:1"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'C', IC2Items.getItem("heat_storage")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR), new Object[] {
			"RYG", "WCM", "IAB",
				'R', "dyeRed",
				'Y', "dyeYellow",
				'G', "dyeGreen",
				'W', "dyeWhite",
				'C', "circuitBasic",
				'M', "dyeMagenta",
				'I', "dyeBlack",
				'A', "dyeCyan",
				'B', "dyeBlue"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("cable","type:tin,insulation:1"),
				'C', IC2Items.getItem("frequency_transmitter"),
				'M', new ItemStack(ItemHelper.infoPanelExtender),
				'R', ItemHelper.itemUpgrade,
				'P', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit), new Object[] { 
			"RF", "PO",
				'P', Items.PAPER,
				'R', "dustRedstone",
				'F', IC2Items.getItem("frequency_transmitter"),
				'O', "dyeRed"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_COUNTER), new Object[] {
			"CF", "PR",
				'P', Items.PAPER,
				'C', "circuitBasic",
				'F', IC2Items.getItem("frequency_transmitter"),
				'R', "dyeOrange" });

		ItemStack kitLiquid = new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID);
		Recipes.advRecipes.addRecipe(kitLiquid, new Object[] {
			"CF", "PB",
				'P', Items.PAPER,
				'C', Items.BUCKET,
				'F', IC2Items.getItem("frequency_transmitter"),
				'B', "dyeBlue" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID_ADVANCED), new Object[] {
			"BKB",
				'B', Items.BUCKET,
				'K', kitLiquid });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_GENERATOR), new Object[] {
			"CF", "PL",
				'P', Items.PAPER,
				'C', IC2Items.getItem("upgrade","energy_storage"),
				'F', IC2Items.getItem("frequency_transmitter"),
				'L', "dyeLightGray"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_REACTOR), new Object[] {
			"DF", "PW", 
				'P', Items.PAPER,
				'D', digitalThermometer,
				'F', IC2Items.getItem("frequency_transmitter"),
				'W', "dyeYellow" });

		if (CrossModLoader.draconicEvolution.modLoaded)
			Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_DRACONIC), new Object[] {
				"RF", "PB", 
					'P', Items.PAPER,
					'R', "dustRedstone",
					'F', IC2Items.getItem("frequency_transmitter"),
					'B', "ingotDraconium" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TEXT), new Object[] {
			" C ", "PFP", " C ",
				'P', Items.PAPER,
				'C', "circuitBasic",
				'F', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TIME), new Object[]{
			" C ", "PWP", " C ",
				'C', Items.CLOCK,
				'P', Items.PAPER,
				'W', IC2Items.getItem("cable","type:tin,insulation:1")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemCardHolder), new Object[] {
			" L ", "LCL", " L ",
				'C', Blocks.CHEST,
				'L', "leather"});

		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				Recipes.advRecipes.addShapelessRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i));

		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
	}
}
