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
}