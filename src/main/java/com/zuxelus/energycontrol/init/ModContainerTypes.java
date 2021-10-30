package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainerTypes {
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnergyControl.MODID);

	public static final RegistryObject<ContainerType<ContainerInfoPanel>> info_panel = CONTAINER_TYPES.register("info_panel", () -> IForgeContainerType.create(ContainerInfoPanel::new));
	public static final RegistryObject<ContainerType<ContainerAdvancedInfoPanel>> info_panel_advanced = CONTAINER_TYPES.register("info_panel_advanced", () -> IForgeContainerType.create(ContainerAdvancedInfoPanel::new));
	public static final RegistryObject<ContainerType<ContainerHoloPanel>> holo_panel = CONTAINER_TYPES.register("holo_panel", () -> IForgeContainerType.create(ContainerHoloPanel::new));
	public static final RegistryObject<ContainerType<ContainerRangeTrigger>> range_trigger = CONTAINER_TYPES.register("range_trigger", () -> IForgeContainerType.create(ContainerRangeTrigger::new));
	public static final RegistryObject<ContainerType<ContainerKitAssembler>> kit_assembler = CONTAINER_TYPES.register("kit_assembler", () -> IForgeContainerType.create(ContainerKitAssembler::new));
	public static final RegistryObject<ContainerType<ContainerTimer>> timer = CONTAINER_TYPES.register("timer", () -> IForgeContainerType.create(ContainerTimer::new));
	public static final RegistryObject<ContainerType<ContainerFluidControlValve>> fluid_control_valve = CONTAINER_TYPES.register("fluid_control_valve", () -> IForgeContainerType.create(ContainerFluidControlValve::new));

	public static final RegistryObject<ContainerType<ContainerPortablePanel>> portable_panel = CONTAINER_TYPES.register("portable_panel", () -> IForgeContainerType.create(ContainerPortablePanel::new));
	public static final RegistryObject<ContainerType<ContainerCardHolder>> card_holder = CONTAINER_TYPES.register("card_holder", () -> IForgeContainerType.create(ContainerCardHolder::new));
}