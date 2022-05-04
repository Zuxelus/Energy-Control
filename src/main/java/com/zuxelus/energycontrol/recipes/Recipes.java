package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class Recipes {

	public static void addRecipes() {
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModItems.itemComponent, 1, ItemComponent.BASIC_CIRCUIT), new ItemStack(ModItems.itemCard, 1, i)));

		addKitsRecipes(new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER));
		addKitAssemblerRecipes();

		addShapedRecipe(ModItems.blockLight, BlockLight.DAMAGE_WHITE_OFF,
			new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeWhite", 'L', Blocks.redstone_lamp });

		ItemStack lampOrange = new ItemStack(ModItems.blockLight, 1, BlockLight.DAMAGE_ORANGE_OFF);
		addShapedRecipe(lampOrange,
			new Object[] { "GGG", "GWG", "GLG", 'G', "paneGlass", 'W', "dyeOrange", 'L', Blocks.redstone_lamp });

		ItemStack howler = new ItemStack(ModItems.blockHowlerAlarm);
		addShapedRecipe(howler, new Object[] {
			"NNN", "ICI", "III",
				'I', "ingotIron",
				'C', "circuitBasic",
				'N', Blocks.noteblock });

		addShapedRecipe(ModItems.blockIndustrialAlarm, new Object[] {
			"   ", "SLS", "SHS",
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'L', lampOrange,
				'H', howler });

		addShapedRecipe(ModItems.blockThermalMonitor,
			new Object[] { "GGG", "GCG", "GRG", 'G', "ingotGold", 'R', Items.redstone, 'C', "circuitAdvanced" });

		addShapedRecipe(ModItems.blockRemoteThermo, new Object[] {
			"IFI", "IMI", "ITI",
				'T', new ItemStack(ModItems.blockThermalMonitor),
				'M', new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING),
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER),
				'I', "ingotIron" });

		addShapedRecipe(ModItems.blockInfoPanel, new Object[] {
			"PPP", "CMC", "IRI",
				'P', "paneGlassLime",
				'C', "circuitBasic",
				'I', "dyeBlack",
				'R', Items.redstone,
				'M', new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING) });

		addShapedRecipe(ModItems.blockInfoPanelExtender,
			new Object[] { "PPP", "WRW", "WWW", 'P', "paneGlassLime", 'R', Items.redstone, 'W', "plankWood" });

		addShapedRecipe(ModItems.blockHoloPanel, new Object[] {
			" S ", "RMR", " S ",
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'R', Items.redstone,
				'M', new ItemStack(ModItems.blockInfoPanel) });

		addShapedRecipe(ModItems.blockHoloPanelExtender, new Object[] {
			" S ", "RMR", " S ",
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'R', Items.redstone,
				'M', new ItemStack(ModItems.blockInfoPanelExtender) });

		ItemStack infoPanelAdvanced = new ItemStack(ModItems.blockInfoPanelAdvanced);
		addShapedRecipe(infoPanelAdvanced, new Object[] { 
			"PPP", "1I2", "CAC",
				'P', "paneGlassLime",
				'I', new ItemStack(ModItems.blockInfoPanel),
				'1', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_COLOR),
				'2', new ItemStack(ModItems.itemUpgrade, 1, ItemUpgrade.DAMAGE_RANGE),
				'A', "circuitAdvanced",
				'C', new ItemStack(Items.coal, 1, 1) });

		ItemStack infoPanelExtenderAdvanced = new ItemStack(ModItems.blockInfoPanelAdvancedExtender);
		addShapedRecipe(infoPanelExtenderAdvanced, new Object[] { 
			"PPP", "CEC", "CCC",
				'P', "paneGlassLime",
				'E', new ItemStack(ModItems.blockInfoPanelExtender),
				'C', new ItemStack(Items.coal, 1, 1) });

		addShapedRecipe(ModItems.blockRangeTrigger, new Object[] {
			"PFP", "AMA", " R ",
				'P', "ingotIron",
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER),
				'A', "circuitAdvanced",
				'M', new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING),
				'R', Items.redstone });

		addShapedRecipe(ModItems.blockKitAssembler, new Object[] {
			"ITI", "RCR", "IFI",
				'T', Blocks.crafting_table,
				'I', "ingotIron",
				'R', Items.redstone,
				'C', Blocks.chest,
				'F', Blocks.furnace });

		addShapedRecipe(ModItems.blockTimer, new Object[] {
			"ITI", "ICI", " R ",
				'T', Items.clock,
				'I', "ingotIron",
				'C', "circuitBasic",
				'R', Items.redstone });

		addShapedRecipe(ModItems.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, new Object[] {
			"   ", "III", "SRS",
				'I', "ingotIron",
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'R', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER) });

		addShapedRecipe(ModItems.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, new Object[] {
			"RYG", "WCM", "IAB", 'R', "dyeRed", 'Y', "dyeYellow", 'G', "dyeGreen", 'W', "dyeWhite",
					'C', "circuitBasic", 'M', "dyeMagenta", 'I', "dyeBlack", 'A', "dyeCyan", 'B', "dyeBlue" });

		addShapedRecipe(ModItems.itemUpgrade, ItemUpgrade.DAMAGE_TOUCH, new Object[] {
			" R ", "SCS", " A ",
				'R', Items.redstone,
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'C', "circuitBasic",
				'A', new ItemStack(Items.coal, 1, 1) });

		addShapedRecipe(ModItems.itemThermometer, new Object[]{
			"IG ", "GWG", " GG",
				'G', "blockGlass",
				'I', "ingotIron",
				'W', Items.water_bucket });

		addShapedRecipe(ModItems.itemPortablePanel, new Object[] {
			"A  ", "CMC", "RPP",
				'A', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
				'C', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER),
				'M', ModItems.blockInfoPanelExtender,
				'R', ModItems.itemUpgrade,
				'P', new ItemStack(Items.coal, 1, 1) });

		addShapedRecipe(ModItems.itemComponent, ItemComponent.RADIO_TRANSMITTER, new Object[] {
				"#C#", " S ",
					'#', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING),
					'C', "circuitBasic",
					'S', "slabWood" });

		addShapedRecipe(ModItems.itemComponent, ItemComponent.STRONG_STRING, new Object[] {
			"#S ", "S#S", " S#",
				'#', Blocks.cactus,
				'S', Items.reeds });
		
		addShapedRecipe(ModItems.itemCardHolder,
			new Object[] { " L ", "LCL", " L ", 'C', Blocks.chest, 'L', "leather" });

		CraftingManager.getInstance().getRecipeList().add(new StorageArrayRecipe());

		CrossModLoader.loadRecipes();
	}

	private static void addKitsRecipes(ItemStack transmitter) {
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_ENERGY,
			new Object[] { "RF", "PO", 'P', Items.paper, 'R', Items.redstone, 'F', transmitter, 'O', "dyeRed" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_COUNTER,
			new Object[] { "CF", "PR", 'P', Items.paper, 'C', "circuitBasic", 'F', transmitter, 'R', "dyeOrange" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_LIQUID,
			new Object[] { "CF", "PB", 'P', Items.paper, 'C', Items.bucket, 'F', transmitter, 'B', "dyeBlue" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_LIQUID_ADVANCED, 
			new Object[] { "BKB", 'B', Items.bucket, 'K', new ItemStack(ModItems.itemKit, 1, ItemCardType.KIT_LIQUID) });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_TOGGLE,
			new Object[] { "DF", "PW", 'P', Items.paper, 'D', Blocks.lever, 'F', transmitter, 'W', "dyeGray" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_VANILLA,
			new Object[] { "DF", "PW", 'P', Items.paper, 'D', "ingotIron", 'F', transmitter, 'W', "dyeGray" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_INVENTORY,
			new Object[] { "DF", "PW", 'P', Blocks.chest, 'D', Blocks.lever, 'F', transmitter, 'W', "dyeGray" });
		addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_REDSTONE,
			new Object[] { "DF", "PW", 'P', Items.redstone, 'D', "ingotIron", 'F', transmitter, 'W', "dyeGray" });

		addShapedRecipe(ModItems.itemCard, ItemCardType.CARD_TEXT,
			new Object[] { " S ", "PCP", " S ", 'P', Items.paper, 'C', "circuitBasic",
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING) });
		addShapedRecipe(ModItems.itemCard, ItemCardType.CARD_TIME,
			new Object[] { " S ", "PCP", " S ", 'P', Items.paper, 'C', Items.clock,
				'S', new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING) });
	}

	private static void addKitAssemblerRecipes() {
		KitAssemblerRecipe.addRecipe("dustRedstone", 2, "ingotGold", 1, new ItemStack(ModItems.itemComponent, 1, ItemComponent.BASIC_CIRCUIT), 1,
			new ItemStack(ModItems.itemComponent, 1, ItemComponent.ADVANCED_CIRCUIT), 300);
		KitAssemblerRecipe.addRecipe("dustRedstone", 2, "ingotIron", 1, Items.flint, 1,
			new ItemStack(ModItems.itemComponent, 1, ItemComponent.BASIC_CIRCUIT), 300);
		KitAssemblerRecipe.addRecipe("ingotIron", 3, "ingotIron", 3, new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING), 3,
			new ItemStack(ModItems.itemComponent, 1, ItemComponent.MACHINE_CASING), 400);
		addKitRecipe(ItemCardType.KIT_ENERGY, ItemCardType.CARD_ENERGY);
		addKitRecipe(ItemCardType.KIT_COUNTER, ItemCardType.CARD_COUNTER);
		addKitRecipe(ItemCardType.KIT_LIQUID, ItemCardType.CARD_LIQUID);
		addKitRecipe(ItemCardType.KIT_LIQUID_ADVANCED, ItemCardType.CARD_LIQUID_ADVANCED);
		addKitRecipe(ItemCardType.KIT_TOGGLE, ItemCardType.CARD_TOGGLE);
		addKitRecipe(ItemCardType.KIT_VANILLA, ItemCardType.CARD_VANILLA);
		addKitRecipe(ItemCardType.KIT_INVENTORY, ItemCardType.CARD_INVENTORY);
		addKitRecipe(ItemCardType.KIT_REDSTONE, ItemCardType.CARD_REDSTONE);
	}

	public static void addShapedRecipe(Block block, Object[] list) {
		addShapedRecipe(new ItemStack(block), list);
	}

	public static void addShapedRecipe(Item item, Object[] list) {
		addShapedRecipe(new ItemStack(item), list);
	}

	public static void addShapedRecipe(Block block, int meta, Object[] list) {
		addShapedRecipe(new ItemStack(block, 1, meta), list);
	}

	public static void addShapedRecipe(Item item, int meta, Object[] list) {
		addShapedRecipe(new ItemStack(item, 1, meta), list);
	}

	public static void addShapedRecipe(ItemStack stack, Object[] list) {
		GameRegistry.addRecipe(new ShapedOreRecipe(stack, list));
	}

	public static void addKitRecipe(int kitId, int cardId) {
		KitAssemblerRecipe.addRecipe(new ItemStack(ModItems.itemCard, 1, cardId), 1,
			new ItemStack(ModItems.itemComponent, 1, ItemComponent.STRONG_STRING), 2,
			new ItemStack(ModItems.itemCard, 1, cardId), 1,
			new ItemStack(ModItems.itemKit, 1, kitId), 1000);
	}
}
