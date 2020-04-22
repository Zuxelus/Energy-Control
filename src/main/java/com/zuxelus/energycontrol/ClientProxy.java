package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ClientProxy extends ServerProxy {
	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		MinecraftForge.EVENT_BUS.register(EnergyControl.config);
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermo.class, new TEThermoRenderer());
		registerBlockModel(ItemHelper.remoteThermo, 0, "remote_thermo");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermo.class, new TERemoteThermoRenderer());		
		registerBlockModel(ItemHelper.infoPanel, 0, "info_panel");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		registerBlockModel(ItemHelper.infoPanelExtender, 0, "info_panel_extender");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());		
		registerBlockModel(ItemHelper.rangeTrigger, 0, "range_trigger");
		
		registerBlockModel(ItemHelper.averageCounter, 0, "average_counter");
		registerBlockModel(ItemHelper.energyCounter, 0, "energy_counter");
		
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_ENERGY, "kit_energy");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_COUNTER, "kit_counter");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_LIQUID, "kit_liquid");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_LIQUID_ADVANCED, "kit_liquid_advanced");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_GENERATOR, "kit_generator");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_REACTOR, "kit_reactor");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_APPENG, "kit_app_eng");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_BIGREACTOR, "kit_big_reactor");

		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_ENERGY, "card_energy");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_COUNTER, "card_counter");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_LIQUID, "card_liquid");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_LIQUID_ADVANCED, "card_liquid_advanced");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR, "card_generator");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR_KINETIC, "card_generator_kinetic");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR_HEAT, "card_generator_heat");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_REACTOR, "card_reactor");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_REACTOR5X5, "card_reactor_5x5");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_TEXT, "card_text");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_TIME, "card_time");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_ENERGY_ARRAY, "card_energy_array");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_LIQUID_ARRAY, "card_liquid_array");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR_ARRAY, "card_generator_array");
		
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "upgrade_range");
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "upgrade_color");
		registerItemModel(ItemHelper.itemThermometer, 0, "thermometer");
		registerItemModel(ItemHelper.itemThermometerDigital, 0, "thermometer_digital");		
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		switch (ID) {
		case BlockDamages.DAMAGE_THERMAL_MONITOR:
			if (tileEntity instanceof TileEntityThermo)
				return new GuiThermalMonitor((TileEntityThermo) tileEntity);
			break;
		case BlockDamages.DAMAGE_HOWLER_ALARM:
			if (tileEntity instanceof TileEntityHowlerAlarm)
				return new GuiHowlerAlarm((TileEntityHowlerAlarm) tileEntity);
		case BlockDamages.DAMAGE_INDUSTRIAL_ALARM:
			if (tileEntity instanceof TileEntityIndustrialAlarm)
				return new GuiIndustrialAlarm((TileEntityIndustrialAlarm) tileEntity);
			break;
		case BlockDamages.DAMAGE_INFO_PANEL:
			if (tileEntity instanceof TileEntityInfoPanel)
				return new GuiInfoPanel(new ContainerInfoPanel(player, (TileEntityInfoPanel) tileEntity));
			break;
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			if (tileEntity instanceof TileEntityRangeTrigger)
				return new GuiRangeTrigger(new ContainerRangeTrigger(player, (TileEntityRangeTrigger) tileEntity));
			break;
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			if (tileEntity instanceof TileEntityRemoteThermo)
				return new GuiRemoteThermo(new ContainerRemoteThermo(player, (TileEntityRemoteThermo) tileEntity));
			break;
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			if (tileEntity instanceof TileEntityAverageCounter)
				return new GuiAverageCounter(new ContainerAverageCounter(player, (TileEntityAverageCounter) tileEntity));
			break;
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			if (tileEntity instanceof TileEntityEnergyCounter)
				return new GuiEnergyCounter(new ContainerEnergyCounter(player, (TileEntityEnergyCounter) tileEntity));
			break;
		}
		return null;
	}

	private static void registerItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}

	private static void registerBlockModel(Block block, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}	
}