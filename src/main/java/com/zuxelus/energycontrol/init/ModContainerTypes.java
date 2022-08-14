package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EnergyControl.MODID);

	public static final RegistryObject<MenuType<ContainerInfoPanel>> info_panel = CONTAINER_TYPES.register("info_panel", () -> IForgeMenuType.create(ContainerInfoPanel::new));
	public static final RegistryObject<MenuType<ContainerAdvancedInfoPanel>> info_panel_advanced = CONTAINER_TYPES.register("info_panel_advanced", () -> IForgeMenuType.create(ContainerAdvancedInfoPanel::new));
	public static final RegistryObject<MenuType<ContainerHoloPanel>> holo_panel = CONTAINER_TYPES.register("holo_panel", () -> IForgeMenuType.create(ContainerHoloPanel::new));
	public static final RegistryObject<MenuType<ContainerRangeTrigger>> range_trigger = CONTAINER_TYPES.register("range_trigger", () -> IForgeMenuType.create(ContainerRangeTrigger::new));
	public static final RegistryObject<MenuType<ContainerRemoteThermalMonitor>> remote_thermo = CONTAINER_TYPES.register("remote_thermo", () -> IForgeMenuType.create(ContainerRemoteThermalMonitor::new));
	public static final RegistryObject<MenuType<ContainerKitAssembler>> kit_assembler = CONTAINER_TYPES.register("kit_assembler", () -> IForgeMenuType.create(ContainerKitAssembler::new));
	public static final RegistryObject<MenuType<ContainerTimer>> timer = CONTAINER_TYPES.register("timer", () -> IForgeMenuType.create(ContainerTimer::new));

	public static final RegistryObject<MenuType<ContainerPortablePanel>> portable_panel = CONTAINER_TYPES.register("portable_panel", () -> IForgeMenuType.create(ContainerPortablePanel::new));
	public static final RegistryObject<MenuType<ContainerCardHolder>> card_holder = CONTAINER_TYPES.register("card_holder", () -> IForgeMenuType.create(ContainerCardHolder::new));
}