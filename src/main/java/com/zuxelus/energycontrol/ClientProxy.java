package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.containers.*;
import com.zuxelus.energycontrol.gui.*;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.renderers.*;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = EnergyControl.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
		SoundHelper.initSound(Minecraft.getInstance().getResourcePackDirectory());
		IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		manager.registerReloadListener(new SoundHelper.SoundLoader());
		SoundHelper.importSound(manager);

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.thermal_monitor.get(), TEThermalMonitorRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.remote_thermo.get(), TERemoteThermoRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel.get(), TileEntityInfoPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_extender.get(), TEInfoPanelExtenderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_advanced.get(), TEAdvancedInfoPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_advanced_extender.get(), TEAdvancedInfoPanelExtenderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.holo_panel.get(), TileEntityHoloPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.timer.get(), TileEntityTimerRenderer::new);

		DeferredWorkQueue.runLater(() -> {
			ScreenManager.<ContainerInfoPanel, GuiInfoPanel>register(ModContainerTypes.info_panel.get(), GuiInfoPanel::new);
			ScreenManager.<ContainerAdvancedInfoPanel, GuiAdvancedInfoPanel>register(ModContainerTypes.info_panel_advanced.get(), GuiAdvancedInfoPanel::new);
			ScreenManager.<ContainerHoloPanel, GuiHoloPanel>register(ModContainerTypes.holo_panel.get(), GuiHoloPanel::new);
			ScreenManager.register(ModContainerTypes.range_trigger.get(), GuiRangeTrigger::new);
			ScreenManager.register(ModContainerTypes.remote_thermo.get(), GuiRemoteThermalMonitor::new);
			ScreenManager.register(ModContainerTypes.energy_counter.get(), GuiEnergyCounter::new);
			ScreenManager.register(ModContainerTypes.kit_assembler.get(), GuiKitAssembler::new);
			ScreenManager.register(ModContainerTypes.timer.get(), GuiTimer::new);
			ScreenManager.register(ModContainerTypes.card_holder.get(), GuiCardHolder::new);
			ScreenManager.register(ModContainerTypes.portable_panel.get(), GuiPortablePanel::new);
			ScreenManager.register(ModContainerTypes.fluid_control_valve.get(), GuiFluidControlValve::new);
		});
	}
}
