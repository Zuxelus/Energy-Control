package com.zuxelus.energycontrol.config;

import com.zuxelus.energycontrol.EnergyControl;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig {
	public ConfigGui(GuiScreen parent) {
		super(parent, new ConfigElement(EnergyControl.config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
				EnergyControl.MODID, false, false,GuiConfig.getAbridgedConfigPath(EnergyControl.config.config.toString()));
	}
}
