package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.blocks.*;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemHelper {
	public static final int KIT_MAX = 6;	
	public static final int KIT_ENERGY = 0;
	public static final int KIT_COUNTER = 1;
	public static final int KIT_LIQUID = 2;
	public static final int KIT_LIQUID_ADVANCED = 3;	
	public static final int KIT_GENERATOR = 4;
	public static final int KIT_REACTOR = 5;
	public static final int KIT_APPENG = 10;
	public static final int KIT_BIGREACTOR = 11;

	public static BlockLight blockLight;
	public static HowlerAlarm howlerAlarm;
	public static IndustrialAlarm industrialAlarm;
	public static ThermalMonitor thermalMonitor;
	public static InfoPanel infoPanel;
	public static InfoPanelExtender infoPanelExtender;
	public static RangeTrigger rangeTrigger;
	public static RemoteThermo remoteThermo;
	public static AverageCounter averageCounter;
	public static EnergyCounter energyCounter;
	public static Item itemKit;
	public static Item itemCard;
	public static Item itemUpgrade;
	public static Item itemThermometer;
	public static Item itemThermometerDigital;
	
	public ItemHelper() {
		registerBlocks();
		registerItems();
		registerTileEntities();
	}

	private void registerBlocks() {
		blockLight = new BlockLight();
		setNames(blockLight,"block_light");
		GameRegistry.register(blockLight);
		GameRegistry.register(new ItemLight(blockLight).setRegistryName("block_light"));
		
		howlerAlarm = new HowlerAlarm();
		setNames(howlerAlarm,"howler_alarm");
		GameRegistry.register(howlerAlarm);
		GameRegistry.register(new ItemBlock(howlerAlarm).setRegistryName("howler_alarm"));
		
		industrialAlarm = new IndustrialAlarm();
		setNames(industrialAlarm,"industrial_alarm");
		GameRegistry.register(industrialAlarm);
		GameRegistry.register(new ItemBlock(industrialAlarm).setRegistryName("industrial_alarm"));
		
		thermalMonitor = new ThermalMonitor();
		setNames(thermalMonitor,"thermal_monitor");
		GameRegistry.register(thermalMonitor);
		GameRegistry.register(new ItemBlock(thermalMonitor).setRegistryName("thermal_monitor"));
		
		infoPanel = new InfoPanel();
		setNames(infoPanel,"info_panel");
		GameRegistry.register(infoPanel);
		GameRegistry.register(new ItemBlock(infoPanel).setRegistryName("info_panel"));
		
		infoPanelExtender = new InfoPanelExtender();
		setNames(infoPanelExtender,"info_panel_extender");
		GameRegistry.register(infoPanelExtender);
		GameRegistry.register(new ItemBlock(infoPanelExtender).setRegistryName("info_panel_extender"));
		
		rangeTrigger = new RangeTrigger();
		setNames(rangeTrigger,"range_trigger");
		GameRegistry.register(rangeTrigger);
		GameRegistry.register(new ItemBlock(rangeTrigger).setRegistryName("range_trigger"));

		remoteThermo = new RemoteThermo();
		setNames(remoteThermo,"remote_thermo");
		GameRegistry.register(remoteThermo);
		GameRegistry.register(new ItemBlock(remoteThermo).setRegistryName("remote_thermo"));
		
		averageCounter = new AverageCounter();
		setNames(averageCounter,"average_counter");
		GameRegistry.register(averageCounter);
		GameRegistry.register(new ItemBlock(averageCounter).setRegistryName("average_counter"));
		
		energyCounter = new EnergyCounter();
		setNames(energyCounter,"energy_counter");
		GameRegistry.register(energyCounter);
		GameRegistry.register(new ItemBlock(energyCounter).setRegistryName("energy_counter"));
	}
	
	private void registerItems() {
		itemKit = new ItemKitMain();
		setNames(itemKit,"item_kit");
		GameRegistry.register(itemKit);
		
		itemCard = new ItemCardMain();
		setNames(itemCard,"item_card");
		GameRegistry.register(itemCard);
		
		itemUpgrade = new ItemUpgrade();
		setNames(itemUpgrade,"item_upgrade");
		GameRegistry.register(itemUpgrade);
		
		itemThermometer = new ItemThermometer();
		setNames(itemThermometer,"thermometer");
		GameRegistry.register(itemThermometer);
		
		itemThermometerDigital = new ItemDigitalThermometer(1, 80, 80);
		setNames(itemThermometerDigital,"thermometer_digital");
		GameRegistry.register(itemThermometerDigital);
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
	
	private void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, "energycontrol:howler_alarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, "energycontrol:industrial_alarm");
		GameRegistry.registerTileEntity(TileEntityThermo.class, "energycontrol:thermo");
		GameRegistry.registerTileEntity(TileEntityRemoteThermo.class, "energycontrol:remote_thermo");
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, "energycontrol:info_panel");
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, "energycontrol:info_panel_extender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, "energycontrol:range_trigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, "energycontrol:average_counter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, "energycontrol:energy_counter");
	}
}
