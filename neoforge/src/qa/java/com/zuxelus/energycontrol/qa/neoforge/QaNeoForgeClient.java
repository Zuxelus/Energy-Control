// SPDX-License-Identifier: GPL-3.0-only
package com.zuxelus.energycontrol.qa.neoforge;
import com.zuxelus.energycontrol.qa.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="energycontrolqa",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class QaNeoForgeClient {
    @SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(()->QaClient.init(25572));}
}
