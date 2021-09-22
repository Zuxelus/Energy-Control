package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.*;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnergyControl.MODID);

	public static final RegistryObject<MenuType<ContainerInfoPanel>> info_panel = CONTAINER_TYPES.register("info_panel", () -> IForgeContainerType.create(ContainerInfoPanel::new));
	public static final RegistryObject<MenuType<ContainerAdvancedInfoPanel>> info_panel_advanced = CONTAINER_TYPES.register("info_panel_advanced", () -> IForgeContainerType.create(ContainerAdvancedInfoPanel::new));
	public static final RegistryObject<MenuType<ContainerHoloPanel>> holo_panel = CONTAINER_TYPES.register("holo_panel", () -> IForgeContainerType.create(ContainerHoloPanel::new));
	public static final RegistryObject<MenuType<ContainerRangeTrigger>> range_trigger = CONTAINER_TYPES.register("range_trigger", () -> IForgeContainerType.create(ContainerRangeTrigger::new));
	public static final RegistryObject<MenuType<ContainerKitAssembler>> kit_assembler = CONTAINER_TYPES.register("kit_assembler", () -> IForgeContainerType.create(ContainerKitAssembler::new));
	public static final RegistryObject<MenuType<ContainerTimer>> timer = CONTAINER_TYPES.register("timer", () -> IForgeContainerType.create(ContainerTimer::new));

	public static final RegistryObject<MenuType<ContainerPortablePanel>> portable_panel = CONTAINER_TYPES.register("portable_panel", () -> IForgeContainerType.create(ContainerPortablePanel::new));
	public static final RegistryObject<MenuType<ContainerCardHolder>> card_holder = CONTAINER_TYPES.register("card_holder", () -> IForgeContainerType.create(ContainerCardHolder::new));
}