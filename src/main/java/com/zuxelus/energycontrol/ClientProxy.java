package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.gui.GuiAdvancedInfoPanel;
import com.zuxelus.energycontrol.gui.GuiInfoPanel;
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
		SoundHelper.initSound(Minecraft.getInstance().gameDir);
		((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(new SoundHelper.SoundLoader());
		SoundHelper.importSound();

		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel.get(), TileEntityInfoPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_extender.get(), TEInfoPanelExtenderRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_advanced.get(), TEAdvancedInfoPanelRenderer::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.info_panel_advanced_extender.get(), TEAdvancedInfoPanelExtenderRenderer::new);

		DeferredWorkQueue.runLater(() -> {
			ScreenManager.registerFactory(ModContainerTypes.info_panel.get(), GuiInfoPanel::new);
			ScreenManager.registerFactory(ModContainerTypes.info_panel_advanced.get(), GuiAdvancedInfoPanel::new);
		});
	}
}
