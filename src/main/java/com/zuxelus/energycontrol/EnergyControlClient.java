package com.zuxelus.energycontrol;


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
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;

public class EnergyControlClient implements ClientModInitializer {
	public static boolean altPressed;

	@Override
	public void onInitializeClient() {
		registerRenders();

		MenuScreens.register(ModContainerTypes.info_panel, GuiInfoPanel::new);
		MenuScreens.register(ModContainerTypes.info_panel_advanced, GuiAdvancedInfoPanel::new);
		MenuScreens.register(ModContainerTypes.holo_panel, GuiHoloPanel::new);
		MenuScreens.register(ModContainerTypes.range_trigger, GuiRangeTrigger::new);
		MenuScreens.register(ModContainerTypes.remote_thermo, GuiRemoteThermalMonitor::new);
		MenuScreens.register(ModContainerTypes.kit_assembler, GuiKitAssembler::new);
		MenuScreens.register(ModContainerTypes.timer, GuiTimer::new);
		MenuScreens.register(ModContainerTypes.card_holder, GuiCardHolder::new);
		MenuScreens.register(ModContainerTypes.portable_panel, GuiPortablePanel::new);

		ChannelHandler.initClient();
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SoundHelper());

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean alt = client.hasAltDown();
			if (altPressed != alt) {
				altPressed = alt;
				ChannelHandler.updateSeverKeys(alt);
			}
		});
	}

	public static void registerRenders() {
		BlockEntityRenderers.register(ModTileEntityTypes.thermal_monitor, TEThermalMonitorRenderer::new);
		BlockEntityRenderers.register(ModTileEntityTypes.remote_thermo, TERemoteThermalMonitorRenderer::new);
		BlockEntityRenderers.register(ModTileEntityTypes.info_panel, TileEntityInfoPanelRenderer::new);
		BlockEntityRenderers.register(ModTileEntityTypes.info_panel_advanced, TEAdvancedInfoPanelRenderer::new);
		BlockEntityRenderers.register(ModTileEntityTypes.holo_panel, TileEntityHoloPanelRenderer::new);
		BlockEntityRenderers.register(ModTileEntityTypes.timer, TileEntityTimerRenderer::new);
		registerPanelModels();
	}

	// Panel bodies are baked into the chunk mesh; block entity renderers only draw the text.
	// Item models ("inventory" variant) keep their JSON models.
	private static void registerPanelModels() {
		PanelModel infoPanel = panelModel("panel_all", "panel_back", TileEntityInfoPanel.GREEN);
		PanelModel infoPanelExtender = panelModel("extender_all", "extender_back", TileEntityInfoPanel.GREEN);
		PanelModel advancedPanel = panelModel("panel_advanced_all", "panel_advanced_side", TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND);
		PanelModel advancedExtender = panelModel("extender_advanced_all", "extender_advanced_back", TileEntityAdvancedInfoPanel.DEFAULT_BACKGROUND);
		ModelLoadingPlugin.register(context -> {
			registerPanelModel(context, "info_panel", infoPanel);
			registerPanelModel(context, "info_panel_extender", infoPanelExtender);
			registerPanelModel(context, "info_panel_advanced", advancedPanel);
			registerPanelModel(context, "info_panel_advanced_extender", advancedExtender);
		});
	}

	// every block state of the panel uses the custom model; the item keeps its JSON model
	private static void registerPanelModel(ModelLoadingPlugin.Context context, String name, PanelModel model) {
		Block block = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(EnergyControl.MODID, name));
		context.registerBlockStateResolver(block, resolver -> block.getStateDefinition().getPossibleStates().forEach(state -> resolver.setModel(state, model)));
	}

	private static PanelModel panelModel(String texture, String particle, int defaultColor) {
		return new PanelModel(Identifier.fromNamespaceAndPath(EnergyControl.MODID, "block/info_panel/" + texture), Identifier.fromNamespaceAndPath(EnergyControl.MODID, "block/info_panel/panel_screen"), Identifier.fromNamespaceAndPath(EnergyControl.MODID, "block/info_panel/" + particle), defaultColor);
	}
}
