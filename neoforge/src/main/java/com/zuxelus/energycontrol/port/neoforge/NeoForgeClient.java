// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.port.neoforge;

import com.zuxelus.energycontrol.port.EnergyControlPort;
import com.zuxelus.energycontrol.port.client.PortClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid=EnergyControlPort.ID, bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public final class NeoForgeClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) { event.enqueueWork(PortClient::init); }
}
