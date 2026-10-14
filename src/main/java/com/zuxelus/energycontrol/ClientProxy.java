package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.renderers.PanelModel;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = EnergyControl.MODID, dist = Dist.CLIENT)
public class ClientProxy {

	public ClientProxy(IEventBus modEventBus, ModContainer container) {
		// enables the Config button in the mod list
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		modEventBus.addListener(ClientProxy::onClientSetup);
		modEventBus.addListener(ClientProxy::registerScreens);
		modEventBus.addListener(ClientProxy::registerRenders);
		modEventBus.addListener(SoundHelper::locatePacks);
		modEventBus.addListener(ClientProxy::registerReloadListeners);
		modEventBus.addListener(ClientProxy::registerGeometryLoaders);
	}

	// "loader": "energycontrol:panel" in the info panel block models
	private static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "panel"), new PanelModel.Loader());
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> SoundHelper.initSoundPack(Minecraft.getInstance().gameDirectory));
	}

	private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener((ResourceManagerReloadListener) SoundHelper::importSound);
	}

	private static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(ModContainerTypes.info_panel.get(), GuiInfoPanel::new);
		event.register(ModContainerTypes.info_panel_advanced.get(), GuiAdvancedInfoPanel::new);
		event.register(ModContainerTypes.holo_panel.get(), GuiHoloPanel::new);
		event.register(ModContainerTypes.range_trigger.get(), GuiRangeTrigger::new);
		event.register(ModContainerTypes.remote_thermo.get(), GuiRemoteThermalMonitor::new);
		event.register(ModContainerTypes.kit_assembler.get(), GuiKitAssembler::new);
		event.register(ModContainerTypes.timer.get(), GuiTimer::new);
		event.register(ModContainerTypes.card_holder.get(), GuiCardHolder::new);
		event.register(ModContainerTypes.portable_panel.get(), GuiPortablePanel::new);
	}

	private static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModTileEntityTypes.thermal_monitor.get(), TEThermalMonitorRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.remote_thermo.get(), TERemoteThermalMonitorRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel.get(), TileEntityInfoPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_advanced.get(), TEAdvancedInfoPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.holo_panel.get(), TileEntityHoloPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.timer.get(), TileEntityTimerRenderer::new);
	}
}
