package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class RecipesNew {

	public static void addRecipes() {
		ItemStack thermalMonitor = new ItemStack(ItemHelper.thermalMonitor);
		Recipes.advRecipes.addRecipe(thermalMonitor, new Object[]{ 
				"LLL", "LCL", "LRL", 
					'L', "plateLead", 
					'R', "dustRedstone", 
					'C', "circuitAdvanced"});

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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.industrialAlarm), new Object[]{ 
				"BBB", "BLB", "BHB", 
					'B', "plateBronze", 
					'L', lampOrange,
					'H', howler });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.remoteThermo), new Object[]{ 
			"IFI", "IMI", "ITI", 
				'T', thermalMonitor, 
				'M', IC2Items.getItem("resource#machine"), 
				'F', IC2Items.getItem("frequency_transmitter"), 
				'I', "plateIron" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.infoPanel), new Object[]{ 
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

		/*ItemStack infoPanelAdvanced = new ItemStack(ItemHelper.infoPanelAdvanced);
		Recipes.advRecipes.addRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC", 
				'P', "paneGlassLime", 
				'I', infoPanel, 
				'1', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced", 
				'C', IC2Items.getItem("carbonPlate")});*/

		/*ItemStack infoPanelExtenderAdvanced = new ItemStack(ItemHelper.infoPanelExtenderAdvanced);
		Recipes.advRecipes.addRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CMC",
				'P', "paneGlassLime",
				'M', IC2Items.getItem("machine"), 
				'E', infoPanelExtender,
				'C', IC2Items.getItem("carbonPlate")});*/

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometer), new Object[]{
			"IG ", "GWG", " GG", 
				'G', "blockGlass", 
				'I', "plateIron", 
				'W', IC2Items.getItem("fluid_cell#water")});

		ItemStack digitalThermometer = new ItemStack(ItemHelper.itemDigitalThermometer);
		Recipes.advRecipes.addRecipe(digitalThermometer, new Object[] { 
			"RI ", "ITI", " IP", 
				'R', "itemRubber",
				'T', ItemHelper.itemThermometer,
				'I', "plateIron",
				'P', IC2Items.getItem("crafting#small_power_unit")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit), new Object[] { 
			"RF", "PO",
				'P', Items.PAPER,
				'R', "dustRedstone",
				'F', IC2Items.getItem("frequency_transmitter"),
				'O', "dyeRed"});

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
				'B', "dyeBlue" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.energyCounter), new Object[] { 
			"IAI", "FTF", 
				'A', "circuitAdvanced", 
				'F', IC2Items.getItem("cable","type:gold,insulation:0"), 
				'T', IC2Items.getItem("te","mv_transformer"), 
				'I', "plateIron"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.averageCounter), new Object[] { 
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("cable","type:gold,insulation:0"),
				'T', IC2Items.getItem("te","mv_transformer"),
				'L', "plateLead"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.rangeTrigger), new Object[] { 
			"PFP", "AMA", " R ", 
				'P', "plateIron", 
				'F', IC2Items.getItem("frequency_transmitter"),
				'A', "circuitAdvanced",
				'M', IC2Items.getItem("resource#machine"),
				'R', "dustRedstone"});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemHelper.KIT_COUNTER), new Object[] { 
			"CF", "PR", 
				'P', Items.PAPER, 
				'C', "circuitBasic", 
				'F', IC2Items.getItem("frequency_transmitter"), 
				'R', "dyeOrange" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemHelper.KIT_LIQUID), new Object[] { 
			"CF", "PB", 
				'P', Items.PAPER, 
				'C', Items.BUCKET,
				'F', IC2Items.getItem("frequency_transmitter"), 
				'B', "dyeBlue" });

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemHelper.KIT_REACTOR), new Object[] { 
				"DF", "PW", 
					'P', Items.PAPER, 
					'D', StackUtil.copyWithWildCard(digitalThermometer), 
					'F', IC2Items.getItem("frequency_transmitter"), 
					'W', "dyeYellow" });		
		
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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemHelper.KIT_GENERATOR), new Object[] { 
			"CF", "PL", 
				'P', Items.PAPER, 
				'C', IC2Items.getItem("upgrade","energy_storage"), 
				'F', IC2Items.getItem("frequency_transmitter"), 
				'L', "dyeLightGray"});

		/*Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemRemoteMonitor), new Object[]{
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("cable","type:tin,insulation:1"),
				'C', IC2Items.getItem("frequency_transmitter"),
				'M', new ItemStack(ItemHelper.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				'R', ItemHelper.itemUpgrade,
				'P', IC2Items.getItem("carbonPlate")});*/
		
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.cards.containsKey(i))
				Recipes.advRecipes.addShapelessRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i));
		
		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
	}
}
