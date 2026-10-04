package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

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

	// replaces the deprecated ScreenHandlerRegistry.registerExtended
	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EnergyControl.MODID, name), new ExtendedScreenHandlerType<>(factory, BlockPos.PACKET_CODEC));
	}
}
