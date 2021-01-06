package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipesNew {

	public static void addRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_WHITE_OFF),
				new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeWhite", 'L', Blocks.redstone_lamp }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF),
				new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeOrange", 'L', Blocks.redstone_lamp }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				new Object[] { "PPP", "WRW", "WWW", 'P', "paneGlassLime", 'R', "dustRedstone", 'W', "plankWood" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_TIMER),
				new Object[] { "ICI", "IBI"," R ", 'I', "ingotIron", 'R', "dustRedstone", 'C', Items.clock, 'B', "circuitBasic" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_LIQUID_ADVANCED), 
				new Object[] { "BKB", 'B', Items.bucket, 'K', new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_LIQUID) }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				new Object[] { "RYG", "WCM", "IAB", 'R', "dyeRed", 'Y', "dyeYellow", 'G', "dyeGreen", 'W', "dyeWhite",
						'C', "circuitBasic", 'M', "dyeMagenta", 'I', "dyeBlack", 'A', "dyeCyan", 'B', "dyeBlue" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemCardHolder),
				new Object[] { " L ", "LCL", " L ", 'C', Blocks.chest, 'L', Items.leather }));
		
		if (CrossModLoader.ic2.getModType() == "IC2Exp")
			RecipesNew.addExpRecipes();
		else
			RecipesNew.addClassicRecipes();

		addKitsRecipes(IC2Items.getItem("frequencyTransmitter"), IC2Items.getItem("energyStorageUpgrade"), IC2Items.getItem("insulatedTinCableItem"));

		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				GameRegistry.addRecipe(new ShapelessOreRecipe(IC2Items.getItem("electronicCircuit"), new ItemStack(ModItems.itemCard, 1, i)));

		GameRegistry.addRecipe(new NanoBowRecipe());
		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());
	}

	private static void addExpRecipes() {
		ItemStack howler = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_HOWLER_ALARM);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "plateIron",
				'M', IC2Items.getItem("elemotor"),
				'N', Blocks.noteblock,
				'C', "circuitBasic"});
		ItemStack lampOrange = new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INDUSTRIAL_ALARM), new Object[] {
			"BBB", "BLB", "BHB",
				'B', "plateBronze",
				'L', lampOrange,
				'H', howler });

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_THERMAL_MONITOR), new Object[] {
			"LLL", "LCL", "LRL", 
				'L', "plateLead", 
				'R', "dustRedstone", 
				'C', "circuitAdvanced"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_REMOTE_THERMO), new Object[] {
			"IFI", "IMI", "ITI",
				'T', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_THERMAL_MONITOR),
				'M', IC2Items.getItem("machine"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'I', "plateIron" });

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL), new Object[] {
			"PPP", "CMC", "IRI",
				'P', "paneGlassLime",
				'C', "circuitBasic",
				'I', "dyeBlack",
				'R', "dustRedstone",
				'M', IC2Items.getItem("machine")});

		ItemStack infoPanelAdvanced = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ADVANCED_PANEL);
		Recipes.advRecipes.addRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC",
				'P', "paneGlassLime",
				'I', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL),
				'1', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced",
				'C', IC2Items.getItem("carbonPlate")});

		ItemStack infoPanelExtenderAdvanced = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ADVANCED_EXTENDER);
		Recipes.advRecipes.addRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CMC",
				'P', "paneGlassLime",
				'M', IC2Items.getItem("machine"),
				'E', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				'C', IC2Items.getItem("carbonPlate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_RANGE_TRIGGER), new Object[] {
			"PFP", "AMA", " R ",
				'P', "plateIron",
				'F', IC2Items.getItem("frequencyTransmitter"),
				'A', "circuitAdvanced",
				'M', IC2Items.getItem("machine"),
				'R', "dustRedstone"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_AVERAGE_COUNTER), new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'L', "plateLead"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ENERGY_COUNTER), new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'I', "plateIron"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.GUI_KIT_ASSEMBER), new Object[] {
			" T ", "CMC", "FAA",
				'T', Blocks.crafting_table,
				'M', IC2Items.getItem("machine"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'C', "circuitBasic",
				'A', IC2Items.getItem("insulatedTinCableItem")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_AFSU), new Object[] {
			"MGM", "IAI", "MGM",
				'I', IC2Items.getItem("iridiumPlate"),
				'G', IC2Items.getItem("glassFiberCableItem"),
				'M', IC2Items.getItem("mfsUnit"),
				'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE)});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockSeedManager, 1, 0), new Object[] {
			" Z ", "#M#", "#C#",
					'Z', IC2Items.getItem("cropnalyzer"),
					'M', IC2Items.getItem("machine"),
					'C', "circuitBasic",
					'S', Items.wheat_seeds,
					'#', Blocks.planks});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockSeedManager, 1, 1), new Object[] {
			"GGG", "#C#", "#Z#",
				'Z', new ItemStack(ModItems.blockSeedManager),
				'C', "circuitAdvanced",
				'G', Blocks.glass,
				'#', Blocks.chest});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemThermometer), new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "plateIron",
				'W', IC2Items.getItem("waterCell")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemThermometerDigital, 1, 32767), new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ModItems.itemThermometer,
				'I', "plateIron",
				'P', IC2Items.getItem("powerunitsmall")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE), new Object[] {
			"CCC", "IFI",
				'I', IC2Items.getItem("insulatedCopperCableItem"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'C', new ItemStack(IC2Items.getItem("reactorCoolantSimple").getItem(), 1, 1)});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_TOUCH), new Object[] {
				" R ", "ICI", " A ",
					'R', "plateTin",
					'I', IC2Items.getItem("insulatedCopperCableItem"),
					'C', "circuitBasic",
					'A', IC2Items.getItem("carbonPlate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("insulatedTinCableItem"),
				'C', IC2Items.getItem("frequencyTransmitter"),
				'M', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				'R', ModItems.itemUpgrade,
				'P', IC2Items.getItem("carbonPlate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemAFB), new Object[] {
				"GIG", "IUI", "GIG",
					'G', IC2Items.getItem("glassFiberCableItem"),
					'I', IC2Items.getItem("iridiumPlate"),
					'U', IC2Items.getItem("uuMatterCell")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemAFSUUpgradeKit), new Object[] {
				"MGM", "IAI", "MGM",
					'I', IC2Items.getItem("iridiumPlate"),
					'G', IC2Items.getItem("glassFiberCableItem"),
					'M', IC2Items.getItem("mfsUnit"),
					'A', new ItemStack(ModItems.itemAFB, 1, Short.MAX_VALUE)});
	}

	private static void addClassicRecipes() {
		ItemStack howler = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_HOWLER_ALARM);
		Recipes.advRecipes.addRecipe(howler, new Object[]{
			"NNN", "IMI", "ICI",
				'I', "ingotIron",
				'M', "ingotIron",
				'N', Blocks.noteblock,
				'C', "circuitBasic"});
		ItemStack lampOrange = new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INDUSTRIAL_ALARM), new Object[] {
			"BBB", "BLB", "BHB",
				'B', "ingotBronze",
				'L', lampOrange,
				'H', howler });

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_THERMAL_MONITOR), new Object[] {
			"LLL", "LCL", "LRL", 
				'L', "ingotGold", 
				'R', "dustRedstone", 
				'C', "circuitAdvanced"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_REMOTE_THERMO), new Object[] {
			"IFI", "IMI", "ITI",
				'T', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_THERMAL_MONITOR),
				'M', IC2Items.getItem("machine"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'I', "ingotIron" });

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL), new Object[] {
			"PPP", "CMC", "IRI",
				'P', "paneGlassLime",
				'C', "circuitBasic",
				'I', "dyeBlack",
				'R', "dustRedstone",
				'M', IC2Items.getItem("machine")});

		ItemStack infoPanelAdvanced = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ADVANCED_PANEL);
		Recipes.advRecipes.addRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC",
				'P', "paneGlassLime",
				'I', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL),
				'1', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced",
				'C', IC2Items.getItem("carbonPlate")});

		ItemStack infoPanelExtenderAdvanced = new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ADVANCED_EXTENDER);
		Recipes.advRecipes.addRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CMC",
				'P', "paneGlassLime",
				'M', IC2Items.getItem("machine"),
				'E', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				'C', IC2Items.getItem("carbonPlate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_RANGE_TRIGGER), new Object[] {
			"PFP", "AMA", " R ",
				'P', "ingotIron",
				'F', IC2Items.getItem("frequencyTransmitter"),
				'A', "circuitAdvanced",
				'M', IC2Items.getItem("machine"),
				'R', "dustRedstone"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_AVERAGE_COUNTER), new Object[] {
			"LAL", "FTF",
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'L', "ingotGold"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ENERGY_COUNTER), new Object[] {
			"IAI", "FTF", 
				'A', "circuitAdvanced",
				'F', IC2Items.getItem("goldCableItem"),
				'T', IC2Items.getItem("mvTransformer"),
				'I', "ingotIron"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.blockMain, 1, BlockDamages.GUI_KIT_ASSEMBER), new Object[] {
			" T ", "CMC", "FAA",
				'T', Blocks.crafting_table,
				'M', IC2Items.getItem("machine"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'C', "circuitBasic",
				'A', IC2Items.getItem("insulatedTinCableItem")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemThermometer), new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "ingotIron",
				'W', IC2Items.getItem("waterCell")});

		ItemStack digitalThermometer = new ItemStack(ModItems.itemThermometerDigital, 1, 32767);
		Recipes.advRecipes.addRecipe(digitalThermometer, new Object[] { 
			"RI ", "ITI", " IP",
				'R', "itemRubber",
				'T', ModItems.itemThermometer,
				'I', "ingotIron",
				'P', "circuitBasic"});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE), new Object[] {
			"CCC", "IFI",
				'I', IC2Items.getItem("insulatedCopperCableItem"),
				'F', IC2Items.getItem("frequencyTransmitter"),
				'C', IC2Items.getItem("heat_storage")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_TOUCH), new Object[] {
				" R ", "ICI", " A ",
					'R', "plateTin",
					'I', IC2Items.getItem("insulatedCopperCableItem"),
					'C', "circuitBasic",
					'A', IC2Items.getItem("carbonPlate")});

		Recipes.advRecipes.addRecipe(new ItemStack(ModItems.itemPortablePanel), new Object[] {
				"A  ", "CMC", "RPP",
				'A', IC2Items.getItem("insulatedTinCableItem"),
				'C', IC2Items.getItem("frequencyTransmitter"),
				'M', new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_INFO_PANEL_EXTENDER),
				'R', ModItems.itemUpgrade,
				'P', IC2Items.getItem("carbonPlate")});
	}

	private static void addKitsRecipes(ItemStack transmitter, ItemStack energyStorage, ItemStack tinCable) {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit),
				new Object[] { "RF", "PO", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'O', "dyeRed" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_COUNTER),
				new Object[] { "CF", "PR", 'P', Items.paper, 'C', "circuitBasic", 'F', transmitter, 'R', "dyeOrange" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_LIQUID),
				new Object[] { "CF", "PB", 'P', Items.paper, 'C', Items.bucket, 'F', transmitter, 'B', "dyeBlue" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_GENERATOR),
				new Object[] { "CF", "PL", 'P', Items.paper, 'C', energyStorage, 'F', transmitter, 'L', "dyeLightGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_TOGGLE),
				new Object[] { "DF", "PW", 'P', Items.paper, 'D', Blocks.lever, 'F', transmitter, 'W', "dyeGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_INVENTORY),
				new Object[] { "RF", "PW", 'P', Items.paper, 'R', Blocks.chest, 'F', transmitter, 'W', "dyeGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_REDSTONE),
				new Object[] { "RF", "PW", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'W', "dyeGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_VANILLA),
				new Object[] { "RF", "PW", 'P', Items.paper, 'R', "ingotIron", 'F', transmitter, 'W', "dyeGray" }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_TEXT),
				new Object[] { " C ", "PFP", " C ", 'P', Items.paper, 'C', "circuitBasic", 'F', tinCable }));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_TIME),
				new Object[] { " C ", "PWP", " C ", 'C', Items.clock, 'P', Items.paper, 'W', tinCable }));

		if (Loader.isModLoaded("IC2"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_REACTOR),
					new Object[] { "DF", "PW", 'P', Items.paper, 'D', new ItemStack(ModItems.itemThermometerDigital, 1, 32767), 'F', transmitter, 'W', "dyeYellow" }));
		if (Loader.isModLoaded("DraconicEvolution"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_DRACONIC),
					new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'B', "ingotDraconium" }));
		if (Loader.isModLoaded("appliedenergistics2"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_APPENG),
					new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'B', CrossModLoader.appEng.getItemStack("fluixCrystal") }));
		if (Loader.isModLoaded("BigReactors"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_BIG_REACTORS),
					new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'B', "ingotYellorium" }));
		if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars"))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_GALACTICRAFT),
					new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone", 'F', transmitter, 'B', CrossModLoader.galacticraft.getItemStack("aluminum_wire") }));
	}
}
