package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.StorageArrayRecipe;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber
public class ItemHelper {
	public static BlockLight blockLight;
	public static HowlerAlarm howlerAlarm;
	public static IndustrialAlarm industrialAlarm;
	public static ThermalMonitor thermalMonitor;
	public static InfoPanel infoPanel;
	public static InfoPanelExtender infoPanelExtender;
	public static AdvancedInfoPanel infoPanelAdvanced;
	public static AdvancedInfoPanelExtender infoPanelAdvancedExtender;
	public static RangeTrigger rangeTrigger;
	public static RemoteThermo remoteThermo;
	public static AverageCounter averageCounter;
	public static EnergyCounter energyCounter;
	public static KitAssembler kitAssembler;
	public static Item itemKit;
	public static Item itemCard;
	public static Item itemUpgrade;
	public static Item itemThermometer;
	public static Item itemThermometerDigital;
	public static Item itemPortablePanel;
	public static Item itemCardHolder;

	@SubscribeEvent
	public static void onBlockRegistry(Register<Block> event) {
		blockLight = new BlockLight();
		setNames(blockLight, "block_light");
		event.getRegistry().register(blockLight);

		howlerAlarm = new HowlerAlarm();
		setNames(howlerAlarm, "howler_alarm");
		event.getRegistry().register(howlerAlarm);

		industrialAlarm = new IndustrialAlarm();
		setNames(industrialAlarm, "industrial_alarm");
		event.getRegistry().register(industrialAlarm);

		thermalMonitor = new ThermalMonitor();
		setNames(thermalMonitor, "thermal_monitor");
		event.getRegistry().register(thermalMonitor);

		infoPanel = new InfoPanel();
		setNames(infoPanel, "info_panel");
		event.getRegistry().register(infoPanel);

		infoPanelExtender = new InfoPanelExtender();
		setNames(infoPanelExtender, "info_panel_extender");
		event.getRegistry().register(infoPanelExtender);

		infoPanelAdvanced = new AdvancedInfoPanel();
		setNames(infoPanelAdvanced, "info_panel_advanced");
		event.getRegistry().register(infoPanelAdvanced);

		infoPanelAdvancedExtender = new AdvancedInfoPanelExtender();
		setNames(infoPanelAdvancedExtender, "info_panel_advanced_extender");
		event.getRegistry().register(infoPanelAdvancedExtender);

		rangeTrigger = new RangeTrigger();
		setNames(rangeTrigger, "range_trigger");
		event.getRegistry().register(rangeTrigger);

		remoteThermo = new RemoteThermo();
		setNames(remoteThermo, "remote_thermo");
		event.getRegistry().register(remoteThermo);

		averageCounter = new AverageCounter();
		setNames(averageCounter, "average_counter");
		event.getRegistry().register(averageCounter);

		energyCounter = new EnergyCounter();
		setNames(energyCounter, "energy_counter");
		event.getRegistry().register(energyCounter);

		kitAssembler = new KitAssembler();
		setNames(kitAssembler, "kit_assembler");
		event.getRegistry().register(kitAssembler);
	}

	@SubscribeEvent
	public static void onItemRegistry(Register<Item> event) {
		event.getRegistry().register(new ItemLight(blockLight).setRegistryName("block_light"));
		event.getRegistry().register(new ItemBlock(howlerAlarm).setRegistryName("howler_alarm"));
		event.getRegistry().register(new ItemBlock(industrialAlarm).setRegistryName("industrial_alarm"));
		event.getRegistry().register(new ItemBlock(thermalMonitor).setRegistryName("thermal_monitor"));
		event.getRegistry().register(new ItemBlock(infoPanel).setRegistryName("info_panel"));
		event.getRegistry().register(new ItemBlock(infoPanelExtender).setRegistryName("info_panel_extender"));
		event.getRegistry().register(new ItemBlock(infoPanelAdvanced).setRegistryName("info_panel_advanced"));
		event.getRegistry().register(new ItemBlock(infoPanelAdvancedExtender).setRegistryName("info_panel_advanced_extender"));
		event.getRegistry().register(new ItemBlock(rangeTrigger).setRegistryName("range_trigger"));
		event.getRegistry().register(new ItemBlock(remoteThermo).setRegistryName("remote_thermo"));
		event.getRegistry().register(new ItemBlock(averageCounter).setRegistryName("average_counter"));
		event.getRegistry().register(new ItemBlock(energyCounter).setRegistryName("energy_counter"));
		event.getRegistry().register(new ItemBlock(kitAssembler).setRegistryName("kit_assembler"));

		itemUpgrade = new ItemUpgrade();
		setNames(itemUpgrade, "item_upgrade");
		event.getRegistry().register(itemUpgrade);

		itemThermometer = new ItemThermometer();
		setNames(itemThermometer, "thermometer");
		event.getRegistry().register(itemThermometer);

		itemThermometerDigital = new ItemDigitalThermometer(1, 80, 80);
		setNames(itemThermometerDigital, "thermometer_digital");
		event.getRegistry().register(itemThermometerDigital);

		itemPortablePanel = new ItemPortablePanel();
		setNames(itemPortablePanel, "portable_panel");
		event.getRegistry().register(itemPortablePanel);

		itemKit = new ItemKitMain();
		((ItemKitMain) itemKit).registerKits();
		setNames(itemKit, "item_kit");
		event.getRegistry().register(itemKit);

		itemCard = new ItemCardMain();
		((ItemCardMain) itemCard).registerCards();
		setNames(itemCard, "item_card");
		event.getRegistry().register(itemCard);

		itemCardHolder = new ItemCardHolder();
		setNames(itemCardHolder, "card_holder");
		event.getRegistry().register(itemCardHolder);
	}

	private static void setNames(Object obj, String name) {
		if (obj instanceof Block) {
			Block block = (Block) obj;
			block.setUnlocalizedName(name);
			block.setRegistryName(name);
		} else if (obj instanceof Item) {
			Item item = (Item) obj;
			item.setUnlocalizedName(name);
			item.setRegistryName(name);
		} else
			throw new IllegalArgumentException("Item or Block required");
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_OFF, "lamp0");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_ON, "lamp1");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_OFF, "lamp2");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_ON, "lamp3");

		registerBlockModel(ItemHelper.howlerAlarm, 0, "howler_alarm");
		registerBlockModel(ItemHelper.industrialAlarm, 0, "industrial_alarm");
		registerBlockModel(ItemHelper.thermalMonitor, 0, "thermal_Monitor");
		registerBlockModel(ItemHelper.remoteThermo, 0, "remote_thermo");
		registerBlockModel(ItemHelper.infoPanel, 0, "info_panel");
		registerBlockModel(ItemHelper.infoPanelExtender, 0, "info_panel_extender");
		registerBlockModel(ItemHelper.infoPanelAdvanced, 0, "info_panel_advanced");
		registerBlockModel(ItemHelper.infoPanelAdvancedExtender, 0, "info_panel_advanced_extender");
		registerBlockModel(ItemHelper.rangeTrigger, 0, "range_trigger");

		registerBlockModel(ItemHelper.averageCounter, 0, "average_counter");
		registerBlockModel(ItemHelper.energyCounter, 0, "energy_counter");
		registerBlockModel(ItemHelper.kitAssembler, 0, "kit_assembler");

		ItemKitMain.registerModels();
		ItemKitMain.registerExtendedModels();
		ItemCardMain.registerModels();
		ItemCardMain.registerExtendedModels();

		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "upgrade_range");
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "upgrade_color");
		registerItemModel(ItemHelper.itemThermometer, 0, "thermometer");
		registerItemModel(ItemHelper.itemThermometerDigital, 0, "thermometer_digital");
		registerItemModel(ItemHelper.itemPortablePanel, 0, "portable_panel");
		registerItemModel(ItemHelper.itemCardHolder, 0, "card_holder");
	}

	public static void registerItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}

	public static void registerExternalItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	private static void registerBlockModel(Block block, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}

	public static void registerTileEntities() { // TODO Change to event
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, "energycontrol:howler_alarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, "energycontrol:industrial_alarm");
		GameRegistry.registerTileEntity(TileEntityThermo.class, "energycontrol:thermo");
		GameRegistry.registerTileEntity(TileEntityRemoteThermo.class, "energycontrol:remote_thermo");
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, "energycontrol:info_panel");
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, "energycontrol:info_panel_extender");
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanel.class, "energycontrol:info_panel_advanced");
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanelExtender.class, "energycontrol:info_panel_advanced_extender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, "energycontrol:range_trigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, "energycontrol:average_counter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, "energycontrol:energy_counter");
		GameRegistry.registerTileEntity(TileEntityKitAssembler.class, "energycontrol:kit_assembler");
	}

	@SubscribeEvent
	public static void registerRecipes(Register<IRecipe> event) {
		event.getRegistry().register(new StorageArrayRecipe().setRegistryName("array_card_recipe"));
	}
}
