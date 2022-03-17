package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
		SoundHelper.initSoundPack(Minecraft.getInstance().gameDirectory);

		MenuScreens.register(ModContainerTypes.info_panel.get(), GuiInfoPanel::new);
		MenuScreens.register(ModContainerTypes.info_panel_advanced.get(), GuiAdvancedInfoPanel::new);
		MenuScreens.register(ModContainerTypes.holo_panel.get(), GuiHoloPanel::new);
		MenuScreens.register(ModContainerTypes.range_trigger.get(), GuiRangeTrigger::new);
		MenuScreens.register(ModContainerTypes.remote_thermo.get(), GuiRemoteThermalMonitor::new);
		MenuScreens.register(ModContainerTypes.kit_assembler.get(), GuiKitAssembler::new);
		MenuScreens.register(ModContainerTypes.timer.get(), GuiTimer::new);
		MenuScreens.register(ModContainerTypes.card_holder.get(), GuiCardHolder::new);
		MenuScreens.register(ModContainerTypes.portable_panel.get(), GuiPortablePanel::new);
	}

	@SubscribeEvent
	public static void registerRenders(RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModTileEntityTypes.thermal_monitor.get(), TEThermalMonitorRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.remote_thermo.get(), TERemoteThermoRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel.get(), TileEntityInfoPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_extender.get(), TEInfoPanelExtenderRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_advanced.get(), TEAdvancedInfoPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.info_panel_advanced_extender.get(), TEAdvancedInfoPanelExtenderRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.holo_panel.get(), TileEntityHoloPanelRenderer::new);
		event.registerBlockEntityRenderer(ModTileEntityTypes.timer.get(), TileEntityTimerRenderer::new);
	}
}
