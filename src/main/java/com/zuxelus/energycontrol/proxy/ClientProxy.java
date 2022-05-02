package com.zuxelus.energycontrol.proxy;

import java.io.File;
import java.util.List;

import com.zuxelus.energycontrol.ClientTickHandler;
import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.ServerTickHandler;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.items.cards.ItemCardHolder;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.tileentities.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy {

	@Override
	public void loadConfig(FMLPreInitializationEvent event) {
		EnergyControl.config = new ConfigHandler();
		MinecraftForge.EVENT_BUS.register(EnergyControl.config);
		EnergyControl.config.init(event.getSuggestedConfigurationFile());
	}

	@Override
	public void registerSpecialRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityThermalMonitor.class, new TEThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRemoteThermalMonitor.class, new TERemoteThermoRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanel.class, new TileEntityInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoPanelExtender.class, new TEInfoPanelExtenderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanel.class, new TEAdvancedInfoPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAdvancedInfoPanelExtender.class, new TEAdvancedInfoPanelExtenderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHoloPanel.class, new TileEntityHoloPanelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTimer.class, new TileEntityTimerRenderer());
		int modelId = RenderingRegistry.getNextAvailableRenderId();
		EnergyControl.instance.modelId = modelId;
		RenderingRegistry.registerBlockHandler(new BlockRenderer(modelId));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID)
		{
		case BlockDamages.GUI_PORTABLE_PANEL:
			return new GuiPortablePanel(new ContainerPortablePanel(player));
		case BlockDamages.GUI_CARD_HOLDER:
			if (player.getCurrentEquippedItem().getItem() instanceof ItemCardHolder)
				return new GuiCardHolder(player);
		}
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
		case BlockDamages.DAMAGE_THERMAL_MONITOR:
			if (te instanceof TileEntityThermalMonitor)
				return new GuiThermalMonitor((TileEntityThermalMonitor) te);
			break;
		case BlockDamages.DAMAGE_HOWLER_ALARM:
			if (te instanceof TileEntityHowlerAlarm)
				return new GuiHowlerAlarm((TileEntityHowlerAlarm) te);
		case BlockDamages.DAMAGE_INDUSTRIAL_ALARM:
			if (te instanceof TileEntityIndustrialAlarm)
				return new GuiIndustrialAlarm((TileEntityIndustrialAlarm) te);
			break;
		case BlockDamages.DAMAGE_INFO_PANEL:
			if (te instanceof TileEntityInfoPanel)
				return new GuiInfoPanel(new ContainerInfoPanel(player, (TileEntityInfoPanel) te));
			break;
		case BlockDamages.DAMAGE_INFO_PANEL_EXTENDER:
			if (te instanceof TileEntityInfoPanelExtender) {
				TileEntityInfoPanel panel = ((TileEntityInfoPanelExtender) te).getCore();
				if (panel != null)
					return new GuiInfoPanel(new ContainerInfoPanel(player, (TileEntityInfoPanel) panel));
			}
			break;
		case BlockDamages.DAMAGE_ADVANCED_EXTENDER:
			if (te instanceof TileEntityAdvancedInfoPanelExtender) {
				TileEntityInfoPanel panel = ((TileEntityAdvancedInfoPanelExtender) te).getCore();
				if (panel != null)
					return new GuiAdvancedInfoPanel(new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) panel));
			}
			return null;
		case BlockDamages.DAMAGE_ADVANCED_PANEL:
			if (te instanceof TileEntityAdvancedInfoPanel)
				return new GuiAdvancedInfoPanel(new ContainerAdvancedInfoPanel(player, (TileEntityAdvancedInfoPanel) te));
			break;
		case BlockDamages.DAMAGE_HOLO_PANEL:
			if (te instanceof TileEntityHoloPanel)
				return new GuiHoloPanel(new ContainerHoloPanel(player, (TileEntityHoloPanel) te));
			break;
		case BlockDamages.DAMAGE_RANGE_TRIGGER:
			if (te instanceof TileEntityRangeTrigger)
				return new GuiRangeTrigger(new ContainerRangeTrigger(player, (TileEntityRangeTrigger) te));
			break;
		case BlockDamages.DAMAGE_REMOTE_THERMO:
			if (te instanceof TileEntityRemoteThermalMonitor)
				return new GuiRemoteThermo(new ContainerRemoteThermo(player, (TileEntityRemoteThermalMonitor) te));
			break;
		case BlockDamages.DAMAGE_AVERAGE_COUNTER:
			if (te instanceof TileEntityAverageCounter)
				return new GuiAverageCounter(new ContainerAverageCounter(player, (TileEntityAverageCounter) te));
			break;
		case BlockDamages.DAMAGE_ENERGY_COUNTER:
			if (te instanceof TileEntityEnergyCounter)
				return new GuiEnergyCounter(new ContainerEnergyCounter(player, (TileEntityEnergyCounter) te));
			break;
		case BlockDamages.GUI_KIT_ASSEMBER:
			if (te instanceof TileEntityKitAssembler)
				return new GuiKitAssembler(new ContainerKitAssembler(player, (TileEntityKitAssembler) te));
			break;
		case BlockDamages.DAMAGE_AFSU:
			if (te instanceof TileEntityAFSU)
				return new GuiAFSU(new ContainerAFSU(player, (TileEntityAFSU) te));
			break;
		case BlockDamages.DAMAGE_SEED_ANALYZER:
			if (te instanceof TileEntitySeedAnalyzer)
				return new GuiSeedAnalyzer(new ContainerSeedAnalyzer(player, (TileEntitySeedAnalyzer) te));
			break;
		case BlockDamages.DAMAGE_SEED_LIBRARY:
			if (te instanceof TileEntitySeedLibrary)
				return new GuiSeedLibrary(new ContainerSeedLibrary(player, (TileEntitySeedLibrary) te));
			break;
		case BlockDamages.DAMAGE_TIMER:
			if (te instanceof TileEntityTimer)
				return new GuiTimer(new ContainerTimer(player, (TileEntityTimer) te));
			break;
		}
		return null;
	}

	@Override
	public void registerEventHandlers() {
		//FMLCommonHandler.instance().bus().register(EnergyControl.config);
		MinecraftForge.EVENT_BUS.register(ClientTickHandler.instance);
		FMLCommonHandler.instance().bus().register(ServerTickHandler.instance); // for single client, FML in 1.7.10
	}

	@Override
	public void importSound(File configFolder) {
		SoundHelper.initSound(configFolder);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new SoundHelper.SoundLoader());
		SoundHelper.importSound();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemName(ItemStack stack) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
		if (list.size() == 0)
			return stack.getItem().getUnlocalizedName();
		return list.get(0);
	}
}
