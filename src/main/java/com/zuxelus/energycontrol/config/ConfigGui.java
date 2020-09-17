package com.zuxelus.energycontrol.config;

import com.zuxelus.energycontrol.EnergyControl;

import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class ConfigGui extends GuiConfig {
	public ConfigGui(GuiScreen parent) {
		super(parent, new ConfigElement(EnergyControl.config.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
				EnergyControl.MODID, false, false,GuiConfig.getAbridgedConfigPath(EnergyControl.config.configuration.toString()));
	}
}