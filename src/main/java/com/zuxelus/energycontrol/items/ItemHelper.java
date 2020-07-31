package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ic2.CrossIC2.IC2Type;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
	public static AFSU afsu;
	public static Item itemKit;
	public static Item itemCard;
	public static Item itemUpgrade;
	public static Item itemThermometer;
	public static Item itemThermometerDigital;
	public static Item itemPortablePanel;
	public static Item itemCardHolder;
	public static Item itemNanoBow;
	public static Item itemAFB;
	public static Item itemAFSUUpgradeKit;

	public static void onBlockRegistry() {
		blockLight = new BlockLight();
		setNames(blockLight, "block_light");
		GameRegistry.register(blockLight);
		GameRegistry.register(new ItemLight(blockLight).setRegistryName("block_light"));

		howlerAlarm = new HowlerAlarm();
		setNames(howlerAlarm, "howler_alarm");
		GameRegistry.register(howlerAlarm);
		GameRegistry.register(new ItemBlock(howlerAlarm).setRegistryName("howler_alarm"));

		industrialAlarm = new IndustrialAlarm();
		setNames(industrialAlarm, "industrial_alarm");
		GameRegistry.register(industrialAlarm);
		GameRegistry.register(new ItemBlock(industrialAlarm).setRegistryName("industrial_alarm"));

		if (Loader.isModLoaded("IC2")) {
			thermalMonitor = new ThermalMonitor();
			setNames(thermalMonitor, "thermal_monitor");
			GameRegistry.register(thermalMonitor);
			GameRegistry.register(new ItemBlock(thermalMonitor).setRegistryName("thermal_monitor"));
		}

		infoPanel = new InfoPanel();
		setNames(infoPanel, TileEntityInfoPanel.NAME);
		GameRegistry.register(infoPanel);
		GameRegistry.register(new ItemBlock(infoPanel).setRegistryName(TileEntityInfoPanel.NAME));

		infoPanelExtender = new InfoPanelExtender();
		setNames(infoPanelExtender, "info_panel_extender");
		GameRegistry.register(infoPanelExtender);
		GameRegistry.register(new ItemBlock(infoPanelExtender).setRegistryName("info_panel_extender"));

		infoPanelAdvanced = new AdvancedInfoPanel();
		setNames(infoPanelAdvanced, TileEntityAdvancedInfoPanel.NAME);
		GameRegistry.register(infoPanelAdvanced);
		GameRegistry.register(new ItemBlock(infoPanelAdvanced).setRegistryName(TileEntityAdvancedInfoPanel.NAME));

		infoPanelAdvancedExtender = new AdvancedInfoPanelExtender();
		setNames(infoPanelAdvancedExtender, "info_panel_advanced_extender");
		GameRegistry.register(infoPanelAdvancedExtender);
		GameRegistry.register(new ItemBlock(infoPanelAdvancedExtender).setRegistryName("info_panel_advanced_extender"));

		rangeTrigger = new RangeTrigger();
		setNames(rangeTrigger, "range_trigger");
		GameRegistry.register(rangeTrigger);
		GameRegistry.register(new ItemBlock(rangeTrigger).setRegistryName("range_trigger"));

		if (Loader.isModLoaded("IC2")) {
			remoteThermo = new RemoteThermo();
			setNames(remoteThermo, "remote_thermo");
			GameRegistry.register(remoteThermo);
			GameRegistry.register(new ItemBlock(remoteThermo).setRegistryName("remote_thermo"));
		}

		averageCounter = new AverageCounter();
		setNames(averageCounter, "average_counter");
		GameRegistry.register(averageCounter);
		GameRegistry.register(new ItemBlock(averageCounter).setRegistryName("average_counter"));

		energyCounter = new EnergyCounter();
		setNames(energyCounter, "energy_counter");
		GameRegistry.register(energyCounter);
		GameRegistry.register(new ItemBlock(energyCounter).setRegistryName("energy_counter"));

		kitAssembler = new KitAssembler();
		setNames(kitAssembler, "kit_assembler");
		GameRegistry.register(kitAssembler);
		GameRegistry.register(new ItemBlock(kitAssembler).setRegistryName("kit_assembler"));

		if (CrossModLoader.ic2.getType() == IC2Type.EXP) {
			afsu = new AFSU();
			setNames(afsu, "afsu");
			GameRegistry.register(afsu);
			GameRegistry.register(new ItemBlock(afsu).setRegistryName("afsu"));
		}
	}

	public static void onItemRegistry() {
		itemUpgrade = new ItemUpgrade();
		setNames(itemUpgrade, "item_upgrade");
		GameRegistry.register(itemUpgrade);

		if (Loader.isModLoaded("IC2")) {
			itemThermometer = new ItemThermometer();
			setNames(itemThermometer, "thermometer");
			GameRegistry.register(itemThermometer);

			itemThermometerDigital = new ItemDigitalThermometer();
			setNames(itemThermometerDigital, "thermometer_digital");
			GameRegistry.register(itemThermometerDigital);
		}

		if (Loader.isModLoaded("IC2"))
			itemNanoBow = new ItemNanoBowIC2();
		else if (Loader.isModLoaded("techreborn"))
			itemNanoBow = new ItemNanoBowTR();
		if (itemNanoBow != null) {
			setNames(itemNanoBow, "nano_bow");
			GameRegistry.register(itemNanoBow);
		}

		if (CrossModLoader.ic2.getType() == IC2Type.EXP) {
			itemAFB = CrossModLoader.ic2.getItem("afb");
			setNames(itemAFB, "afb");
			GameRegistry.register(itemAFB);

			itemAFSUUpgradeKit = CrossModLoader.ic2.getItem("afsu_upgrade_kit");
			setNames(itemAFSUUpgradeKit, "afsu_upgrade_kit");
			GameRegistry.register(itemAFSUUpgradeKit);
		}

		itemPortablePanel = new ItemPortablePanel();
		setNames(itemPortablePanel, "portable_panel");
		GameRegistry.register(itemPortablePanel);

		itemKit = new ItemKitMain();
		((ItemKitMain) itemKit).registerKits();
		setNames(itemKit, "item_kit");
		GameRegistry.register(itemKit);

		itemCard = new ItemCardMain();
		((ItemCardMain) itemCard).registerCards();
		setNames(itemCard, "item_card");
		GameRegistry.register(itemCard);

		itemCardHolder = new ItemCardHolder();
		setNames(itemCardHolder, "card_holder");
		GameRegistry.register(itemCardHolder);
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

	public static void onModelRegister() {
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_OFF, "lamp0");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_ON, "lamp1");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_OFF, "lamp2");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_ON, "lamp3");

		registerBlockModel(ItemHelper.howlerAlarm, 0, "howler_alarm");
		registerBlockModel(ItemHelper.industrialAlarm, 0, "industrial_alarm");
		if (Loader.isModLoaded("IC2")) {
			registerBlockModel(ItemHelper.thermalMonitor, 0, "thermal_monitor");
			registerBlockModel(ItemHelper.remoteThermo, 0, "remote_thermo");
		}
		registerBlockModel(ItemHelper.infoPanel, 0, TileEntityInfoPanel.NAME);
		registerBlockModel(ItemHelper.infoPanelExtender, 0, "info_panel_extender");
		registerBlockModel(ItemHelper.infoPanelAdvanced, 0, TileEntityAdvancedInfoPanel.NAME);
		registerBlockModel(ItemHelper.infoPanelAdvancedExtender, 0, "info_panel_advanced_extender");
		registerBlockModel(ItemHelper.rangeTrigger, 0, "range_trigger");

		registerBlockModel(ItemHelper.averageCounter, 0, "average_counter");
		registerBlockModel(ItemHelper.energyCounter, 0, "energy_counter");
		registerBlockModel(ItemHelper.kitAssembler, 0, "kit_assembler");
		if (CrossModLoader.ic2.getType() == IC2Type.EXP)
			registerBlockModel(ItemHelper.afsu, 0, "afsu");

		ItemKitMain.registerModels();
		ItemKitMain.registerExtendedModels();
		ItemCardMain.registerModels();
		ItemCardMain.registerExtendedModels();

		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "upgrade_range");
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "upgrade_color");
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_TOUCH, "upgrade_touch");
		if (Loader.isModLoaded("IC2")) {
			registerItemModel(ItemHelper.itemThermometer, 0, "thermometer");
			registerItemModel(ItemHelper.itemThermometerDigital, 0, "thermometer_digital");
		}
		registerItemModel(ItemHelper.itemPortablePanel, 0, "portable_panel");
		registerItemModel(ItemHelper.itemCardHolder, 0, "card_holder");
		if (ItemHelper.itemNanoBow != null)
			registerItemModel(ItemHelper.itemNanoBow, 0, "nano_bow");
		if (CrossModLoader.ic2.getType() == IC2Type.EXP) {
			registerItemModel(ItemHelper.itemAFB, 0, "afb");
			registerItemModel(ItemHelper.itemAFSUUpgradeKit, 0, "afsu_upgrade_kit");
		}
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
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, EnergyControl.MODID + ":howler_alarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, EnergyControl.MODID + ":industrial_alarm");
		if (Loader.isModLoaded("IC2")) {
			GameRegistry.registerTileEntity(TileEntityThermo.class, EnergyControl.MODID + ":thermo");
			GameRegistry.registerTileEntity(TileEntityRemoteThermo.class, EnergyControl.MODID + ":remote_thermo");
		}
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, EnergyControl.MODID + ":" + TileEntityInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_extender");
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanel.class, EnergyControl.MODID + ":" + TileEntityAdvancedInfoPanel.NAME);
		GameRegistry.registerTileEntity(TileEntityAdvancedInfoPanelExtender.class, EnergyControl.MODID + ":info_panel_advanced_extender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, EnergyControl.MODID + ":range_trigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, EnergyControl.MODID + ":average_counter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, EnergyControl.MODID + ":energy_counter");
		GameRegistry.registerTileEntity(TileEntityKitAssembler.class, EnergyControl.MODID + ":kit_assembler");
		if (CrossModLoader.ic2.getType() == IC2Type.EXP)
			GameRegistry.registerTileEntity(TileEntityAFSU.class, EnergyControl.MODID + ":afsu");
	}
}
