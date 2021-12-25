package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModContainerTypes {
	public static final ScreenHandlerType<ContainerInfoPanel> info_panel = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "info_panel"), ContainerInfoPanel::new);
	public static final ScreenHandlerType<ContainerAdvancedInfoPanel> info_panel_advanced = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "info_panel_advanced"), ContainerAdvancedInfoPanel::new);
	public static final ScreenHandlerType<ContainerHoloPanel> holo_panel = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "holo_panel"), ContainerHoloPanel::new);
	public static final ScreenHandlerType<ContainerRangeTrigger> range_trigger = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "range_trigger"), ContainerRangeTrigger::new);
	public static final ScreenHandlerType<ContainerKitAssembler> kit_assembler = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "kit_assembler"), ContainerKitAssembler::new);
	public static final ScreenHandlerType<ContainerTimer> timer = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "timer"), ContainerTimer::new);

	public static final ScreenHandlerType<ContainerPortablePanel> portable_panel = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "portable_panel"), ContainerPortablePanel::new);
	public static final ScreenHandlerType<ContainerCardHolder> card_holder = ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, "card_holder"), ContainerCardHolder::new);
}