package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModContainerTypes {
	private static final PacketCodec<RegistryByteBuf, Hand> HAND_CODEC = PacketCodecs.BOOL.<RegistryByteBuf>cast().xmap(
			offHand -> offHand ? Hand.OFF_HAND : Hand.MAIN_HAND, hand -> hand == Hand.OFF_HAND);

	public static final ScreenHandlerType<ContainerInfoPanel> info_panel = register("info_panel", ContainerInfoPanel::new);
	public static final ScreenHandlerType<ContainerAdvancedInfoPanel> info_panel_advanced = register("info_panel_advanced", ContainerAdvancedInfoPanel::new);
	public static final ScreenHandlerType<ContainerHoloPanel> holo_panel = register("holo_panel", ContainerHoloPanel::new);
	public static final ScreenHandlerType<ContainerRangeTrigger> range_trigger = register("range_trigger", ContainerRangeTrigger::new);
	public static final ScreenHandlerType<ContainerRemoteThermalMonitor> remote_thermo = register("remote_thermo", ContainerRemoteThermalMonitor::new);
	public static final ScreenHandlerType<ContainerKitAssembler> kit_assembler = register("kit_assembler", ContainerKitAssembler::new);
	public static final ScreenHandlerType<ContainerTimer> timer = register("timer", ContainerTimer::new);

	// opened from an item, so it needs the hand instead of a position
	public static final ScreenHandlerType<ContainerPortablePanel> portable_panel = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EnergyControl.MODID, "portable_panel"),
			new ExtendedScreenHandlerType<>(ContainerPortablePanel::new, HAND_CODEC));
	public static final ScreenHandlerType<ContainerCardHolder> card_holder = register("card_holder", ContainerCardHolder::new);

	// replaces the deprecated ScreenHandlerRegistry.registerExtended
	private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, BlockPos> factory) {
		return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EnergyControl.MODID, name), new ExtendedScreenHandlerType<>(factory, BlockPos.PACKET_CODEC));
	}
}
