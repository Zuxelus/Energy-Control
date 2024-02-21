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
	public int infoPanelRefreshPeriod;
	public int rangeTriggerRefreshPeriod;
	public int thermalMonitorRefreshPeriod;
	public boolean useCustomSounds;
	public int alarmPause;
	public boolean disableRangeCheck;
	public boolean disableCircuitRecipe;

	public void init(File configFile) {
		if (config == null)
			config = new Configuration(configFile);
		loadConfig();
	}

	private void loadConfig() {
		try {
			howlerAlarmRange = config.getInt("howlerAlarmRange", Configuration.CATEGORY_GENERAL, 64, 0, 256, "", "ec.config.howlerAlarmRange");
			maxAlarmRange = config.getInt("maxAlarmRange", Configuration.CATEGORY_GENERAL, 128, 0, 256, "", "ec.config.maxAlarmRange");
			allowedAlarms = config.getString("allowedAlarms", Configuration.CATEGORY_GENERAL, "default,sci-fi,siren", "", "ec.config.allowedAlarms").replaceAll(" ", "");
			infoPanelRefreshPeriod = config.getInt("infoPanelRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.screenRefreshPeriod");
			rangeTriggerRefreshPeriod = config.getInt("rangeTriggerRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.rangeTriggerRefreshPeriod");
			thermalMonitorRefreshPeriod = config.getInt("thermalMonitorRefreshPeriod", Configuration.CATEGORY_GENERAL, 20, 0, 2000, "", "ec.config.thermalMonitorRefreshPeriod");
			useCustomSounds = config.getBoolean("useCustomSounds", Configuration.CATEGORY_GENERAL, false, "Update file config\\alarms\\assets\\energycontrol\\sounds.json to see your sounds in game", "ec.config.useCustomSounds");
			alarmPause = config.getInt("alarmPause", Configuration.CATEGORY_GENERAL, 60, 0, 2000, "", "ec.config.alarmPause");
			disableRangeCheck = config.getBoolean("disableRangeCheck", Configuration.CATEGORY_GENERAL, false, "", "ec.config.disableRangeCheck");
			disableCircuitRecipe = config.getBoolean("disableCircuitRecipe", Configuration.CATEGORY_GENERAL, false, "", "ec.config.disableCircuitRecipe");
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
