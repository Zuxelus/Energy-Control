// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.neoforge;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.client.PanelScreen;
import com.zuxelus.energycontrol.port.client.PanelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid=EnergyControlPort.ID, bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public final class NeoForgeClient {
    // Register at the loader's dedicated events; client setup is too late to add
    // Architectury's RegisterMenuScreensEvent listener on NeoForge 1.21.1.
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) { event.register(EnergyControlPort.PANEL_MENU.get(),PanelScreen::new); event.register(EnergyControlPort.PORTABLE_MENU.get(),com.zuxelus.energycontrol.port.client.PortableScreen::new); }
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(EnergyControlPort.PANEL_ENTITY.get(),PanelRenderer::new); }
}
