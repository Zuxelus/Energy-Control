package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RecipesNew {

	public static void addRecipes() {
		/*ItemStack thermalMonitor = new ItemStack(ItemHelper.thermalMonitor);
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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometer), new Object[]{
			"IG ", "GWG", " GG", 
				'G', "blockGlass", 
				'I', "plateIron", 
				'W', IC2Items.getItem("fluid_cell#water")});

		ItemStack digitalThermometer = new ItemStack(ItemHelper.itemThermometerDigital);
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
				'R', "dustRedstone"});*/

		ItemKitMain.registerRecipes();
		ItemCardMain.registerRecipes();
		
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				Recipes.advRecipes.addShapelessRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i));
	}
}
