package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModContainerTypes {
	private static final StreamCodec<RegistryFriendlyByteBuf, InteractionHand> HAND_CODEC = ByteBufCodecs.BOOL.<RegistryFriendlyByteBuf>cast().map(
			offHand -> offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, hand -> hand == InteractionHand.OFF_HAND);

	public static final MenuType<ContainerInfoPanel> info_panel = register("info_panel", ContainerInfoPanel::new);
	public static final MenuType<ContainerAdvancedInfoPanel> info_panel_advanced = register("info_panel_advanced", ContainerAdvancedInfoPanel::new);
	public static final MenuType<ContainerHoloPanel> holo_panel = register("holo_panel", ContainerHoloPanel::new);
	public static final MenuType<ContainerRangeTrigger> range_trigger = register("range_trigger", ContainerRangeTrigger::new);
	public static final MenuType<ContainerRemoteThermalMonitor> remote_thermo = register("remote_thermo", ContainerRemoteThermalMonitor::new);
	public static final MenuType<ContainerKitAssembler> kit_assembler = register("kit_assembler", ContainerKitAssembler::new);
	public static final MenuType<ContainerTimer> timer = register("timer", ContainerTimer::new);

	// opened from an item, so they need the hand instead of a position
	public static final MenuType<ContainerPortablePanel> portable_panel = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "portable_panel"),
			new ExtendedMenuType<>(ContainerPortablePanel::new, HAND_CODEC));
	public static final MenuType<ContainerCardHolder> card_holder = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "card_holder"),
			new ExtendedMenuType<>(ContainerCardHolder::new, HAND_CODEC));

	// replaces the deprecated ScreenHandlerRegistry.registerExtended
	private static <T extends AbstractContainerMenu> MenuType<T> register(String name, ExtendedMenuType.ExtendedFactory<T, BlockPos> factory) {
		return Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(EnergyControl.MODID, name), new ExtendedMenuType<>(factory, BlockPos.STREAM_CODEC));
	}
}
