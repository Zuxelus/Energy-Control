package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class ModContainerTypes {
	public static final ScreenHandlerType<ContainerInfoPanel> info_panel = register("info_panel", ContainerInfoPanel::new);
	public static final ScreenHandlerType<ContainerAdvancedInfoPanel> info_panel_advanced = register("info_panel_advanced", ContainerAdvancedInfoPanel::new);
	public static final ScreenHandlerType<ContainerHoloPanel> holo_panel = register("holo_panel", ContainerHoloPanel::new);
	public static final ScreenHandlerType<ContainerRangeTrigger> range_trigger = register("range_trigger", ContainerRangeTrigger::new);
	public static final ScreenHandlerType<ContainerRemoteThermalMonitor> remote_thermo = register("remote_thermo", ContainerRemoteThermalMonitor::new);
	public static final ScreenHandlerType<ContainerKitAssembler> kit_assembler = register("kit_assembler", ContainerKitAssembler::new);
	public static final ScreenHandlerType<ContainerTimer> timer = register("timer", ContainerTimer::new);

	public static final ScreenHandlerType<ContainerPortablePanel> portable_panel = register("portable_panel", ContainerPortablePanel::new);
	public static final ScreenHandlerType<ContainerCardHolder> card_holder = register("card_holder", ContainerCardHolder::new);

	// ExtendedScreenHandlerType is not public API in Fabric API for 1.17
	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
		return ScreenHandlerRegistry.registerExtended(new Identifier(EnergyControl.MODID, name), factory);
	}
}
