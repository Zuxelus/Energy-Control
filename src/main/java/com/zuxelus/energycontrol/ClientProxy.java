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
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends ServerProxy {
	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		MinecraftForge.EVENT_BUS.register(EnergyControl.config);
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}
	
	@Override	
	public void registerModels() {
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_OFF, "lamp0");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_WHITE_ON, "lamp1");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_OFF, "lamp2");
		registerBlockModel(ItemHelper.blockLight, BlockLight.DAMAGE_ORANGE_ON, "lamp3");
		
		registerBlockModel(ItemHelper.howlerAlarm, 0, "howlerAlarm");
		registerBlockModel(ItemHelper.industrialAlarm, 0, "industrialAlarm");
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIndustrialAlarm.class, new TEIndustrialAlarmRenderer());
		registerBlockModel(ItemHelper.thermalMonitor, 0, "thermalMonitor");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermo.class, new TEThermoRenderer());
		registerBlockModel(ItemHelper.remoteThermo, 0, "remoteThermo");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermo.class, new TERemoteThermoRenderer());		
		registerBlockModel(ItemHelper.infoPanel, 0, "infoPanel");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		registerBlockModel(ItemHelper.infoPanelExtender, 0, "infoPanelExtender");
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());		
		registerBlockModel(ItemHelper.rangeTrigger, 0, "rangeTrigger");
		
		registerBlockModel(ItemHelper.averageCounter, 0, "averageCounter");
		registerBlockModel(ItemHelper.energyCounter, 0, "energyCounter");
		
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_ENERGY, "kitEnergy");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_COUNTER, "kitCounter");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_LIQUID, "kitLiquid");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_GENERATOR, "kitGenerator");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_REACTOR, "kitReactor");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_APPENG, "kitAppEng");
		registerItemModel(ItemHelper.itemKit, ItemHelper.KIT_BIGREACTOR, "kitBigReactor");

		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_ENERGY, "cardEnergy");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_COUNTER, "cardCounter");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_LIQUID, "cardLiquid");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR, "cardGenerator");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_REACTOR, "cardReactor");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_REACTOR5X5, "cardReactor5x5");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_TEXT, "cardText");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_TIME, "cardTime");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_ENERGY_ARRAY, "cardEnergyArray");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_LIQUID_ARRAY, "cardLiquidArray");
		registerItemModel(ItemHelper.itemCard, ItemCardType.CARD_GENERATOR_ARRAY, "cardGeneratorArray");
		
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_RANGE, "upgradeRange");
		registerItemModel(ItemHelper.itemUpgrade, ItemUpgrade.DAMAGE_COLOR, "upgradeColor");
		registerItemModel(ItemHelper.itemThermometer, 0, "itemThermometer");
		registerItemModel(ItemHelper.itemDigitalThermometer, 0, "itemDigitalThermometer");		
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

	private void registerItemModel(Item item, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(name, "inventory"));
	}

	private void registerBlockModel(Block block, int meta, String name) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(EnergyControl.MODID + ":" + name, "inventory"));
	}	
}