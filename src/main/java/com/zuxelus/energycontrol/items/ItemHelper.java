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
	public static final int KIT_GENERATOR = 3;
	public static final int KIT_REACTOR = 4;
	public static final int KIT_APPENG = 5;
	public static final int KIT_BIGREACTOR = 6;

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
	public static Item itemDigitalThermometer;
	//public static Item itemRemoteMonitor;
	
	public ItemHelper() {
		registerBlocks();
		registerItems();
		registerTileEntities();
	}

	private void registerBlocks() {
		blockLight = new BlockLight();
		setNames(blockLight,"blockLight");
		GameRegistry.register(blockLight);
		GameRegistry.register(new ItemLight(blockLight).setRegistryName("blockLight"));
		
		howlerAlarm = new HowlerAlarm();
		setNames(howlerAlarm,"howlerAlarm");
		GameRegistry.register(howlerAlarm);
		GameRegistry.register(new ItemBlock(howlerAlarm).setRegistryName("howlerAlarm"));
		
		industrialAlarm = new IndustrialAlarm();
		setNames(industrialAlarm,"industrialAlarm");
		GameRegistry.register(industrialAlarm);
		GameRegistry.register(new ItemBlock(industrialAlarm).setRegistryName("industrialAlarm"));
		
		thermalMonitor = new ThermalMonitor();
		setNames(thermalMonitor,"thermalMonitor");
		GameRegistry.register(thermalMonitor);
		GameRegistry.register(new ItemBlock(thermalMonitor).setRegistryName("thermalMonitor"));
		
		infoPanel = new InfoPanel();
		setNames(infoPanel,"infoPanel");
		GameRegistry.register(infoPanel);
		GameRegistry.register(new ItemBlock(infoPanel).setRegistryName("infoPanel"));
		
		infoPanelExtender = new InfoPanelExtender();
		setNames(infoPanelExtender,"infoPanelExtender");
		GameRegistry.register(infoPanelExtender);
		GameRegistry.register(new ItemBlock(infoPanelExtender).setRegistryName("infoPanelExtender"));
		
		rangeTrigger = new RangeTrigger();
		setNames(rangeTrigger,"rangeTrigger");
		GameRegistry.register(rangeTrigger);
		GameRegistry.register(new ItemBlock(rangeTrigger).setRegistryName("rangeTrigger"));

		remoteThermo = new RemoteThermo();
		setNames(remoteThermo,"remoteThermo");
		GameRegistry.register(remoteThermo);
		GameRegistry.register(new ItemBlock(remoteThermo).setRegistryName("remoteThermo"));
		
		averageCounter = new AverageCounter();
		setNames(averageCounter,"averageCounter");
		GameRegistry.register(averageCounter);
		GameRegistry.register(new ItemBlock(averageCounter).setRegistryName("averageCounter"));	
		
		energyCounter = new EnergyCounter();
		setNames(energyCounter,"energyCounter");
		GameRegistry.register(energyCounter);
		GameRegistry.register(new ItemBlock(energyCounter).setRegistryName("energyCounter"));
	}
	
	private void registerItems() {
		itemKit = new ItemKitMain();
		setNames(itemKit,"itemKit");
		GameRegistry.register(itemKit);
		
		itemCard = new ItemCardMain();
		setNames(itemCard,"itemCard");
		GameRegistry.register(itemCard);
		
		itemUpgrade = new ItemUpgrade();
		setNames(itemUpgrade,"itemUpgrade");
		GameRegistry.register(itemUpgrade);
		
		itemThermometer = new ItemThermometer();
		setNames(itemThermometer,"itemThermometer");
		GameRegistry.register(itemThermometer);
		
		itemDigitalThermometer = new ItemDigitalThermometer(1, 80, 80);
		setNames(itemDigitalThermometer,"itemDigitalThermometer");
		GameRegistry.register(itemDigitalThermometer);

		/*itemRemoteMonitor = new ItemThermometer();
		setNames(itemRemoteMonitor,"itemRemoteMonitor");
		GameRegistry.register(itemRemoteMonitor);*/
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
		GameRegistry.registerTileEntity(TileEntityHowlerAlarm.class, "ECHowlerAlarm");
		GameRegistry.registerTileEntity(TileEntityIndustrialAlarm.class, "ECIndustrialAlarm");
		GameRegistry.registerTileEntity(TileEntityThermo.class, "ECThermo");
		GameRegistry.registerTileEntity(TileEntityRemoteThermo.class, "ECRemoteThermo");
		GameRegistry.registerTileEntity(TileEntityInfoPanel.class, "ECInfoPanel");
		GameRegistry.registerTileEntity(TileEntityInfoPanelExtender.class, "ECInfoPanelExtender");
		GameRegistry.registerTileEntity(TileEntityRangeTrigger.class, "ECRangeTrigger");
		GameRegistry.registerTileEntity(TileEntityAverageCounter.class, "ECAverageCounter");
		GameRegistry.registerTileEntity(TileEntityEnergyCounter.class, "ECEnergyCounter");
	}
}
