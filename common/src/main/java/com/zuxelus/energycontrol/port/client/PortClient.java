// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.client;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;

public final class PortClient {
    private PortClient() {}
    public static void init() {
        MenuRegistry.registerScreenFactory(EnergyControlPort.PANEL_MENU.get(),PanelScreen::new);
        BlockEntityRendererRegistry.register(EnergyControlPort.PANEL_ENTITY.get(),PanelRenderer::new);
    }
}
