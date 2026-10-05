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
import com.zuxelus.energycontrol.gui.GuiRemoteThermalMonitor;
import com.zuxelus.energycontrol.gui.GuiTimer;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.renderers.TEAdvancedInfoPanelRenderer;
import com.zuxelus.energycontrol.renderers.TERemoteThermalMonitorRenderer;
import com.zuxelus.energycontrol.renderers.PanelModel;
import com.zuxelus.energycontrol.renderers.TEThermalMonitorRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityHoloPanelRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityInfoPanelRenderer;
import com.zuxelus.energycontrol.renderers.TileEntityTimerRenderer;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.energycontrol.utils.SoundHelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

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
		ScreenRegistry.register(ModContainerTypes.remote_thermo, GuiRemoteThermalMonitor::new);
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
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.thermal_monitor, TEThermalMonitorRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.remote_thermo, TERemoteThermalMonitorRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.info_panel, TileEntityInfoPanelRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.info_panel_advanced, TEAdvancedInfoPanelRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.holo_panel, TileEntityHoloPanelRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModTileEntityTypes.timer, TileEntityTimerRenderer::new);
		registerPanelModels();
	}

	// Panel bodies are baked into the chunk mesh; block entity renderers only draw the text.
	// Item models ("inventory" variant) keep their JSON models.
	private static void registerPanelModels() {
		PanelModel infoPanel = panelModel("panel_all", "panel_back", TileEntityInfoPanel.GREEN);
		PanelModel infoPanelExtender = panelModel("extender_all", "extender_back", TileEntityInfoPanel.GREEN);
		PanelModel advancedPanel = panelModel("panel_advanced_all", "panel_advanced_side", TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND);
		PanelModel advancedExtender = panelModel("extender_advanced_all", "extender_advanced_back", TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND);
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(manager -> (modelId, context) -> {
			if (!modelId.getNamespace().equals(EnergyControl.MODID) || modelId.getVariant().equals("inventory"))
				return null;
			switch (modelId.getPath()) {
			case "info_panel":
				return infoPanel;
			case "info_panel_extender":
				return infoPanelExtender;
			case "info_panel_advanced":
				return advancedPanel;
			case "info_panel_advanced_extender":
				return advancedExtender;
			}
			return null;
		});
	}

	private static PanelModel panelModel(String texture, String particle, int defaultColor) {
		return new PanelModel(new Identifier(EnergyControl.MODID, "block/info_panel/" + texture), new Identifier(EnergyControl.MODID, "block/info_panel/panel_screen"), new Identifier(EnergyControl.MODID, "block/info_panel/" + particle), defaultColor);
	}
}
