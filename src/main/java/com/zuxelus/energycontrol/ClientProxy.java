package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockLight;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

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

public class ClientProxy extends ServerProxy {
	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		MinecraftForge.EVENT_BUS.register(EnergyControl.config);
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}
	
	@Override
	public void registerSpecialRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermo.class, new TEThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermo.class, new TERemoteThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());
	}
	
	@Override
	public void registerExtendedModels() {
		ItemKitMain.registerExtendedModels();
		ItemCardMain.registerExtendedModels();
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
	
	@Override
	public void importSound() {
		SoundHelper.importSound();
	}
}