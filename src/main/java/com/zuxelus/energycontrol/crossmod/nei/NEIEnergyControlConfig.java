package com.zuxelus.energycontrol.crossmod.nei;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.gui.GuiKitAssembler;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEIEnergyControlConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return EnergyControl.NAME;
	}

	@Override
	public String getVersion() {
		return EnergyControl.VERSION;
	}

	@Override
	public void loadConfig() {
		API.registerRecipeHandler(new KitAssemblerRecipeHandler());
		API.registerUsageHandler(new KitAssemblerRecipeHandler());
		//API.registerGuiOverlay(GuiKitAssembler.class, EnergyControl.MODID + ".kit_assembler");
		//API.registerGuiOverlayHandler(GuiKitAssembler.class, new DefaultOverlayHandler(), EnergyControl.MODID + ".kit_assembler");
		GuiContainerManager.addTooltipHandler(new TooltipHandler()); // 1.7.10
	}

}
