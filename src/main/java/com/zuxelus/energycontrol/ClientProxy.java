package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.network.PacketAlarm;
import com.zuxelus.energycontrol.network.PacketCard;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.utils.SoundHelper;
import com.zuxelus.energycontrol.utils.SoundLoader;
import com.zuxelus.zlib.network.PacketTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;

@Mod(value = EnergyControl.MODID, dist = Dist.CLIENT)
public class ClientProxy {

	public ClientProxy(IEventBus modEventBus) {
		modEventBus.addListener(ClientProxy::onClientSetup);
		modEventBus.addListener(ClientProxy::registerScreens);
		modEventBus.addListener(ClientProxy::registerRenders);
		modEventBus.addListener(ClientProxy::registerPayloadHandlers);
		modEventBus.addListener(SoundLoader::locatePacks);
		modEventBus.addListener(ClientProxy::registerReloadListeners);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> SoundHelper.initSoundPack(Minecraft.getInstance().gameDirectory));
	}

	private static void registerReloadListeners(AddClientReloadListenersEvent event) {
		event.addListener(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "alarms"), (ResourceManagerReloadListener) SoundHelper::importSound);
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
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_extender.get(), TEInfoPanelExtenderRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_advanced.get(), TEAdvancedInfoPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_advanced_extender.get(), TEAdvancedInfoPanelExtenderRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.holo_panel.get(), TileEntityHoloPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.timer.get(), TileEntityTimerRenderer::new);
	}

	private static void registerPayloadHandlers(RegisterClientPayloadHandlersEvent event) {
		event.register(PacketCard.TYPE, PacketCard::handleClient);
		event.register(PacketTileEntity.TYPE, PacketTileEntity::handleClient);
		event.register(PacketAlarm.TYPE, PacketAlarm::handle);
	}
}
