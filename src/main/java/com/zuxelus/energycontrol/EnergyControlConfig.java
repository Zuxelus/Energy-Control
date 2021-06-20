package com.zuxelus.energycontrol;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = EnergyControl.MODID)
@LangKey("ec.config.title")
@EventBusSubscriber(modid = EnergyControl.MODID)
public class EnergyControlConfig {
    
    @RangeInt(min = 0, max = 256)
    @LangKey("ec.config.howlerAlarmRange")
    public static int howlerAlarmRange = 64;
    
    @RangeInt(min = 0, max = 256)
    @LangKey("ec.config.maxAlarmRange")
    public static int maxAlarmRange = 128;
    
    @LangKey("ec.config.allowedAlarms")
    public static String[] allowedAlarms = new String[] { "default", "sci-fi", "siren" };
    
    @RangeInt(min = 1, max = 1000)
    @LangKey("ec.config.remoteThermalMonitorEnergyConsumption")
    public static int remoteThermalMonitorEnergyConsumption = 1;
    
    @RangeInt(min = 0, max = 2000)
    @LangKey("ec.config.screenRefreshPeriod")
    public static int infoPanelRefreshPeriod = 20;
    
    @RangeInt(min = 0, max = 2000)
    @LangKey("ec.config.rangeTriggerRefreshPeriod")
    public static int rangeTriggerRefreshPeriod = 20;
    
    @LangKey("ec.config.useCustomSounds")
    public static boolean useCustomSounds = false;
    
    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(EnergyControl.MODID)) {
            ConfigManager.sync(EnergyControl.MODID, Config.Type.INSTANCE);
        }
    }
}
