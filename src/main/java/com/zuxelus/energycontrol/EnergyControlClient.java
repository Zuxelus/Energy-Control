package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.containers.InfoPanelContainer;
import com.zuxelus.energycontrol.gui.InfoPanelScreen;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.ChannelHandler;
import com.zuxelus.energycontrol.renderers.InfoPanelBlockEntityRenderer;
import com.zuxelus.energycontrol.renderers.InfoPanelExtenderBERenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class EnergyControlClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_BLOCK_ENTITY, InfoPanelBlockEntityRenderer::new);
		BlockEntityRendererRegistry.INSTANCE.register(ModItems.INFO_PANEL_EXTENDER_BLOCK_ENTITY, InfoPanelExtenderBERenderer::new);
		ScreenProviderRegistry.INSTANCE.<InfoPanelContainer>registerFactory(ModItems.INFO_PANEL,
				(container) -> new InfoPanelScreen(container, MinecraftClient.getInstance().player.inventory,
						new TranslatableText(ModItems.INFO_PANEL_TRANSLATION_KEY)));
		ChannelHandler.initClient();
	}

}
