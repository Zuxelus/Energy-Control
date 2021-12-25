package com.zuxelus.energycontrol;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.gui.GuiAdvancedInfoPanel;
import com.zuxelus.energycontrol.gui.GuiCardHolder;
import com.zuxelus.energycontrol.gui.GuiHoloPanel;
import com.zuxelus.energycontrol.gui.GuiInfoPanel;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;
import com.zuxelus.energycontrol.gui.GuiPortablePanel;
import com.zuxelus.energycontrol.gui.GuiRangeTrigger;
import com.zuxelus.energycontrol.gui.GuiTimer;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.renderers.TEAdvancedInfoPanelExtenderRenderer;
import com.zuxelus.energycontrol.renderers.TEAdvancedInfoPanelRenderer;
import com.zuxelus.energycontrol.renderers.TEInfoPanelExtenderRenderer;
import com.zuxelus.energycontrol.renderers.TEThermalMonitorRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityHoloPanelRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityInfoPanelRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityTimerRenderer;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;

public class EnergyControlClient implements ClientModInitializer {
	public static boolean altPressed;
	public static List<BlockEntity> holo_panels = Lists.newArrayList();

	@Override
	public void onInitializeClient() {
		registerRenders();

		ScreenRegistry.register(ModContainerTypes.info_panel, GuiInfoPanel::new);
		ScreenRegistry.register(ModContainerTypes.info_panel_advanced, GuiAdvancedInfoPanel::new);
		ScreenRegistry.register(ModContainerTypes.holo_panel, GuiHoloPanel::new);
		ScreenRegistry.register(ModContainerTypes.range_trigger, GuiRangeTrigger::new);
		ScreenRegistry.register(ModContainerTypes.kit_assembler, GuiKitAssembler::new);
		ScreenRegistry.register(ModContainerTypes.timer, GuiTimer::new);
		ScreenRegistry.register(ModContainerTypes.card_holder, GuiCardHolder::new);
		ScreenRegistry.register(ModContainerTypes.portable_panel, GuiPortablePanel::new);

		ChannelHandler.initClient();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SoundHelper());

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean alt = Screen.hasAltDown();
			if (altPressed != alt) {
				altPressed = alt;
				ChannelHandler.updateSeverKeys(alt);
			}
		});
	}

	public static void registerRenders() {
		BlockEntityRendererRegistry.register(ModTileEntityTypes.thermal_monitor, TEThermalMonitorRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.info_panel, TileEntityInfoPanelRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.info_panel_extender, TEInfoPanelExtenderRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.info_panel_advanced, TEAdvancedInfoPanelRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.info_panel_advanced_extender, TEAdvancedInfoPanelExtenderRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.holo_panel, TileEntityHoloPanelRenderer::new);
		BlockEntityRendererRegistry.register(ModTileEntityTypes.timer, TileEntityTimerRenderer::new);
	}
}
