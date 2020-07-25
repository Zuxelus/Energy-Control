package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ic2.CrossIC2;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipesNew {

	public static void addRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_WHITE_OFF),
				new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeWhite", 'L', Blocks.REDSTONE_LAMP }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF),
				new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeOrange", 'L', Blocks.REDSTONE_LAMP }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.infoPanelExtender),
				new Object[] { "PPP", "WRW", "WWW", 'P', "paneGlassLime", 'R', "dustRedstone", 'W', "plankWood" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID_ADVANCED), 
				new Object[] { "BKB", 'B', Items.BUCKET, 'K', new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				new Object[] { "RYG", "WCM", "IAB", 'R', "dyeRed", 'Y', "dyeYellow", 'G', "dyeGreen", 'W', "dyeWhite",
						'C', "circuitBasic", 'M', "dyeMagenta", 'I', "dyeBlack", 'A', "dyeCyan", 'B', "dyeBlue" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemCardHolder),
				new Object[] { " L ", "LCL", " L ", 'C', Blocks.CHEST, 'L', "leather" }));
		
		if (Loader.isModLoaded("IC2")) {
			if (CrossModLoader.ic2.getType() == CrossIC2.IC2Type.EXP)
				RecipesNew.addExpRecipes();
			else
				RecipesNew.addClassicRecipes();
			addKitsRecipes(IC2Items.getItem("frequency_transmitter"), IC2Items.getItem("upgrade", "energy_storage"), IC2Items.getItem("cable","type:tin,insulation:1"));
			
			for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
				if (ItemCardMain.containsCard(i))
					GameRegistry.addRecipe(new ShapelessOreRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i)));
			GameRegistry.addRecipe(new NanoBowRecipe());
		} else if (Loader.isModLoaded("techreborn")) {
			for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
				if (ItemCardMain.containsCard(i))
					GameRegistry.addRecipe(new ShapelessOreRecipe(CrossModLoader.techReborn.getItemStack("circuit"), new ItemStack(ItemHelper.itemCard, 1, i)));
			GameRegistry.addRecipe(new NanoBowRecipeTR());
		}
		
		if (Loader.isModLoaded("techreborn")) {
			RecipesNew.addTRRecipes();
			addKitsRecipes(CrossModLoader.techReborn.getItemStack("transmitter"), CrossModLoader.techReborn.getItemStack("energy_storage"), CrossModLoader.techReborn.getItemStack("tin_cable"));
		}
		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
	}

	private static void addExpRecipes() {
		ItemStack howler = new ItemStack(ItemHelper.howlerAlarm);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "plateIron",
				'M', IC2Items.getItem("crafting#electric_motor"),
				'N', Blocks.NOTEBLOCK,
				'C', "circuitBasic"});
		ItemStack lampOrange = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.afsu), new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("crafting","iridium"),
				'G', IC2Items.getItem("cable","type:glass,insulation:0"),
				'M', IC2Items.getItem("te","mfsu"),
				'A', new ItemStack(ItemHelper.itemAFB, 1, Short.MAX_VALUE)});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometer), new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "plateIron",
				'W', IC2Items.getItem("fluid_cell#water")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemThermometerDigital, 1, 32767), new Object[] { 
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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_TOUCH), new Object[] {
				" R ", "ICI", " A ",
					'R', IC2Items.getItem("upgrade","redstone_inverter"),
					'I', IC2Items.getItem("cable","type:copper,insulation:1"),
					'C', "circuitBasic",
					'A', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("cable","type:tin,insulation:1"),
				'C', IC2Items.getItem("frequency_transmitter"),
				'M', new ItemStack(ItemHelper.infoPanelExtender),
				'R', ItemHelper.itemUpgrade,
				'P', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemAFB), new Object[] {
				"GIG", "IUI", "GIG",
					'G', IC2Items.getItem("cable","type:glass,insulation:0"),
					'I', IC2Items.getItem("crafting","iridium"),
					'U', IC2Items.getItem("fluid_cell","ic2uu_matter")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemAFSUUpgradeKit), new Object[] {
				"MGM", "IAI", "MGM",
					'I', IC2Items.getItem("crafting","iridium"),
					'G', IC2Items.getItem("cable","type:glass,insulation:0"),
					'M', IC2Items.getItem("upgrade_kit","mfsu"),
					'A', new ItemStack(ItemHelper.itemAFB, 1, Short.MAX_VALUE)});
	}

	private static void addClassicRecipes() {
		ItemStack howler = new ItemStack(ItemHelper.howlerAlarm);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "ingotIron",
				'M', "ingotIron",
				'N', Blocks.NOTEBLOCK,
				'C', "circuitBasic"});
		ItemStack lampOrange = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
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

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_TOUCH), new Object[] {
				" R ", "ICI", " A ",
					'R', "plateTin",
					'I', IC2Items.getItem("cable","type:copper,insulation:1"),
					'C', "circuitBasic",
					'A', IC2Items.getItem("crafting","carbon_plate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ItemHelper.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("cable","type:tin,insulation:1"),
				'C', IC2Items.getItem("frequency_transmitter"),
				'M', new ItemStack(ItemHelper.infoPanelExtender),
				'R', ItemHelper.itemUpgrade,
				'P', IC2Items.getItem("crafting","carbon_plate")});
	}

	private static void addTRRecipes() {
		ItemStack frame = CrossModLoader.techReborn.getItemStack("frame");
		ItemStack carbonPlate = CrossModLoader.techReborn.getItemStack("carbon_plate");
		if (!Loader.isModLoaded("IC2")) {
			ItemStack howler = new ItemStack(ItemHelper.howlerAlarm);
			GameRegistry.addRecipe(new ShapedOreRecipe(howler, new Object[] { "NNN", "IMI", "ICI", 'I', "plateIron", 'M',
					"plateIron", 'N', Blocks.NOTEBLOCK, 'C', "circuitBasic" }));
			ItemStack lampOrange = new ItemStack(ItemHelper.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.industrialAlarm),
					new Object[] { "BBB", "BLB", "BHB", 'B', "plateBronze", 'L', lampOrange, 'H', howler }));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.thermalMonitor),
					new Object[] { "LLL", "LCL", "LRL", 'L', "plateLead", 'R', "dustRedstone", 'C', "circuitAdvanced" }));
		}
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.remoteThermo),
				new Object[] { "IFI", "IMI", "ITI", 'T', new ItemStack(ItemHelper.thermalMonitor), 'M',
						frame, 'F', CrossModLoader.techReborn.getItemStack("transmitter"), 'I', "plateIron" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.infoPanel),
				new Object[] { "PPP", "CMC", "IRI", 'P', "paneGlassLime", 'C', "circuitBasic", 'I', "dyeBlack", 'R', "dustRedstone", 'M', frame }));
		ItemStack infoPanelAdvanced = new ItemStack(ItemHelper.infoPanelAdvanced);
		GameRegistry.addRecipe(new ShapedOreRecipe(infoPanelAdvanced,
				new Object[] { "PPP", "1I2", "CAC", 'P', "paneGlassLime", 'I', new ItemStack(ItemHelper.infoPanel), '1',
						new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR), '2',
						new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE), 'A', "circuitAdvanced", 'C', carbonPlate }));
		ItemStack infoPanelExtenderAdvanced = new ItemStack(ItemHelper.infoPanelAdvancedExtender);
		GameRegistry.addRecipe(new ShapedOreRecipe(infoPanelExtenderAdvanced,
				new Object[] { "PPP", "CEC", "CMC", 'P', "paneGlassLime", 'M', frame, 'E', new ItemStack(ItemHelper.infoPanelExtender), 'C', carbonPlate }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.rangeTrigger),
				new Object[] { "PFP", "AMA", " R ", 'P', "plateIron", 'F', CrossModLoader.techReborn.getItemStack("transmitter"),
						'A', "circuitAdvanced", 'M', frame, 'R', "dustRedstone" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.averageCounter),
				new Object[] { "LAL", "FTF", 'A', "circuitAdvanced", 'F',
						CrossModLoader.techReborn.getItemStack("gold_cable"), 'T',
						CrossModLoader.techReborn.getItemStack("mv_transformer"), 'L', "plateLead" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.energyCounter),
				new Object[] { "IAI", "FTF", 'A', "circuitAdvanced", 'F',
						CrossModLoader.techReborn.getItemStack("gold_cable"), 'T',
						CrossModLoader.techReborn.getItemStack("mv_transformer"), 'I', "plateIron" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.kitAssembler),
				new Object[] { " T ", "CMC", "FAA", 'T', Blocks.CRAFTING_TABLE, 'M', frame, 'F',
						CrossModLoader.techReborn.getItemStack("transmitter"), 'C', "circuitBasic", 'A',
						CrossModLoader.techReborn.getItemStack("tin_cable") }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				new Object[] { "CCC", "IFI", 'I', CrossModLoader.techReborn.getItemStack("copper_cable"), 'F',
						CrossModLoader.techReborn.getItemStack("transmitter"), 'C', CrossModLoader.techReborn.getItemStack("coolant_simple") }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemUpgrade, 1, ItemUpgrade.DAMAGE_TOUCH),
				new Object[] { " R ", "ICI", " A ", 'R', "plateTin", 'I',
						CrossModLoader.techReborn.getItemStack("copper_cable"), 'C', "circuitBasic", 'A', carbonPlate }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemPortablePanel),
				new Object[] { "A  ", "CMC", "RPP", 'A', CrossModLoader.techReborn.getItemStack("tin_cable"), 'C',
						CrossModLoader.techReborn.getItemStack("transmitter"), 'M',
						new ItemStack(ItemHelper.infoPanelExtender), 'R', ItemHelper.itemUpgrade, 'P', carbonPlate }));
	}

	private static void addKitsRecipes(ItemStack transmitter, ItemStack energyStorage, ItemStack tinCable) {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit),
				new Object[] { "RF", "PO", 'P', Items.PAPER, 'R', "dustRedstone", 'F', transmitter, 'O', "dyeRed" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_COUNTER),
				new Object[] { "CF", "PR", 'P', Items.PAPER, 'C', "circuitBasic", 'F', transmitter, 'R', "dyeOrange" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_LIQUID),
				new Object[] { "CF", "PB", 'P', Items.PAPER, 'C', Items.BUCKET, 'F', transmitter, 'B', "dyeBlue" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_GENERATOR),
				new Object[] { "CF", "PL", 'P', Items.PAPER, 'C', energyStorage, 'F', transmitter, 'L', "dyeLightGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_TOGGLE),
				new Object[] { "DF", "PW", 'P', Items.PAPER, 'D', Blocks.LEVER, 'F', transmitter, 'W', "dyeGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TEXT),
				new Object[] { " C ", "PFP", " C ", 'P', Items.PAPER, 'C', "circuitBasic", 'F', tinCable }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_TIME),
				new Object[] { " C ", "PWP", " C ", 'C', Items.CLOCK, 'P', Items.PAPER, 'W', tinCable }));

		if (Loader.isModLoaded("IC2"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_REACTOR),
					new Object[] { "DF", "PW", 'P', Items.PAPER, 'D', new ItemStack(ItemHelper.itemThermometerDigital, 1, 32767), 'F', transmitter, 'W', "dyeYellow" }));
		if (Loader.isModLoaded("draconicevolution"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_DRACONIC),
					new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone", 'F', transmitter, 'B', "ingotDraconium" }));
		if (Loader.isModLoaded("appliedenergistics2"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_APPENG),
					new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone", 'F', transmitter, 'B', CrossModLoader.appEng.getItemStack("fluixCrystal") }));
		if (Loader.isModLoaded("bigreactors"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_BIG_REACTORS),
					new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone", 'F', transmitter, 'B', "ingotYellorium" }));
		if (Loader.isModLoaded("galacticraftcore") && Loader.isModLoaded("galacticraftplanets"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHelper.itemKit, 1, ItemCardType.KIT_GALACTICRAFT),
					new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone", 'F', transmitter, 'B', CrossModLoader.galacticraft.getItemStack("aluminum_wire") }));
	}
}
