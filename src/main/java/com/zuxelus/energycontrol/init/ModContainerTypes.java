package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, EnergyControl.MODID);

	public static final DeferredHolder<MenuType<?>, MenuType<ContainerInfoPanel>> info_panel = CONTAINER_TYPES.register("info_panel", () -> IMenuTypeExtension.create(ContainerInfoPanel::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerAdvancedInfoPanel>> info_panel_advanced = CONTAINER_TYPES.register("info_panel_advanced", () -> IMenuTypeExtension.create(ContainerAdvancedInfoPanel::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerHoloPanel>> holo_panel = CONTAINER_TYPES.register("holo_panel", () -> IMenuTypeExtension.create(ContainerHoloPanel::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerRangeTrigger>> range_trigger = CONTAINER_TYPES.register("range_trigger", () -> IMenuTypeExtension.create(ContainerRangeTrigger::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerRemoteThermalMonitor>> remote_thermo = CONTAINER_TYPES.register("remote_thermo", () -> IMenuTypeExtension.create(ContainerRemoteThermalMonitor::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerKitAssembler>> kit_assembler = CONTAINER_TYPES.register("kit_assembler", () -> IMenuTypeExtension.create(ContainerKitAssembler::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerTimer>> timer = CONTAINER_TYPES.register("timer", () -> IMenuTypeExtension.create(ContainerTimer::new));

	public static final DeferredHolder<MenuType<?>, MenuType<ContainerPortablePanel>> portable_panel = CONTAINER_TYPES.register("portable_panel", () -> IMenuTypeExtension.create(ContainerPortablePanel::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCardHolder>> card_holder = CONTAINER_TYPES.register("card_holder", () -> IMenuTypeExtension.create(ContainerCardHolder::new));
}