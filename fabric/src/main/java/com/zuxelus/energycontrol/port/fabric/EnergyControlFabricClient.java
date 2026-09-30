// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.fabric;
import com.zuxelus.energycontrol.port.client.PortClient;
import net.fabricmc.api.ClientModInitializer;
public final class EnergyControlFabricClient implements ClientModInitializer {
    @Override public void onInitializeClient() { PortClient.init(); }
}
