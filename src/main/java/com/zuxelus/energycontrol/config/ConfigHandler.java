package com.zuxelus.energycontrol.config;

import java.io.File;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	public Configuration config;

	public int howlerAlarmRange;
	public int maxAlarmRange;
	public String allowedAlarms;	
	public int remoteThermalMonitorEnergyConsumption;
	public int infoPanelRefreshPeriod;
	public int rangeTriggerRefreshPeriod;
	public int SMPMaxAlarmRange;
	public boolean useCustomSounds;

	public void init(File configFile) {
		if (config == null)
			config = new Configuration(configFile);
		loadConfig();
	}

	private void loadConfig() {
		try {
			howlerAlarmRange = config.get(Configuration.CATEGORY_GENERAL, "howlerAlarmRange", 64).getInt();
			maxAlarmRange = config.get(Configuration.CATEGORY_GENERAL, "maxAlarmRange", 128).getInt();
			allowedAlarms = config.get(Configuration.CATEGORY_GENERAL, "allowedAlarms", "default,sci-fi,siren").getString().replaceAll(" ", "");
			remoteThermalMonitorEnergyConsumption = config.get(Configuration.CATEGORY_GENERAL, "remoteThermalMonitorEnergyConsumption", 1).getInt();
			infoPanelRefreshPeriod = config.get(Configuration.CATEGORY_GENERAL, "infoPanelRefreshPeriod",20).getInt();
			rangeTriggerRefreshPeriod = config.get(Configuration.CATEGORY_GENERAL, "rangeTriggerRefreshPeriod", 20).getInt();
			SMPMaxAlarmRange = config.get(Configuration.CATEGORY_GENERAL, "SMPMaxAlarmRange", 256).getInt();
			useCustomSounds = config.get(Configuration.CATEGORY_GENERAL, "useCustomSounds", false).getBoolean();
		} catch (Exception e) {
			EnergyControl.logger.error("Mod has a problem loading it's configuration", e);
		} finally {
			if (config.hasChanged())
				config.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(EnergyControl.MODID))
			loadConfig();
	}
}
