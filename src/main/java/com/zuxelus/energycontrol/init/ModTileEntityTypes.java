package com.zuxelus.energycontrol.init;

import java.util.Set;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModTileEntityTypes {
	public static final BlockEntityType<TileEntityHowlerAlarm> howler_alarm = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "howler_alarm"),
		new BlockEntityType<>(TileEntityHowlerAlarm::new, Set.of(ModItems.howler_alarm)));
	public static final BlockEntityType<TileEntityIndustrialAlarm> industrial_alarm = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "industrial_alarm"),
		new BlockEntityType<>(TileEntityIndustrialAlarm::new, Set.of(ModItems.industrial_alarm)));
	public static final BlockEntityType<TileEntityThermalMonitor> thermal_monitor = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "thermal_monitor"),
		new BlockEntityType<>(TileEntityThermalMonitor::new, Set.of(ModItems.thermal_monitor)));
	public static final BlockEntityType<TileEntityRangeTrigger> range_trigger = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "range_trigger"),
		new BlockEntityType<>(TileEntityRangeTrigger::new, Set.of(ModItems.range_trigger)));
	public static final BlockEntityType<TileEntityRemoteThermalMonitor> remote_thermo = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "remote_thermo"),
		new BlockEntityType<>(TileEntityRemoteThermalMonitor::new, Set.of(ModItems.remote_thermo)));
	public static final BlockEntityType<TileEntityInfoPanel> info_panel = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "info_panel"),
		new BlockEntityType<>(TileEntityInfoPanel::new, Set.of(ModItems.info_panel))); 
	public static final BlockEntityType<TileEntityInfoPanelExtender> info_panel_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_extender"),
		new BlockEntityType<>(TileEntityInfoPanelExtender::new, Set.of(ModItems.info_panel_extender)));
	public static final BlockEntityType<TileEntityAdvancedInfoPanel> info_panel_advanced = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced"),
		new BlockEntityType<>(TileEntityAdvancedInfoPanel::new, Set.of(ModItems.info_panel_advanced)));
	public static final BlockEntityType<TileEntityAdvancedInfoPanelExtender> info_panel_advanced_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced_extender"),
		new BlockEntityType<>(TileEntityAdvancedInfoPanelExtender::new, Set.of(ModItems.info_panel_advanced_extender)));
	public static final BlockEntityType<TileEntityHoloPanel> holo_panel = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel"),
		new BlockEntityType<>(TileEntityHoloPanel::new, Set.of(ModItems.holo_panel)));
	public static final BlockEntityType<TileEntityHoloPanelExtender> holo_panel_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel_extender"),
		new BlockEntityType<>(TileEntityHoloPanelExtender::new, Set.of(ModItems.holo_panel_extender)));
	//public static final BlockEntityType<TileEntityAverageCounter> average_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "average_counter"),
	//	BlockEntityType.Builder.create(TileEntityAverageCounter::new, ModItems.average_counter).build(null));
	//public static final BlockEntityType<TileEntityEnergyCounter> energy_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "energy_counter"),
	//	BlockEntityType.Builder.create(TileEntityEnergyCounter::new, ModItems.energy_counter).build(null));
	public static final BlockEntityType<TileEntityKitAssembler> kit_assembler = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"),
		new BlockEntityType<>(TileEntityKitAssembler::new, Set.of(ModItems.kit_assembler)));
	public static final BlockEntityType<TileEntityTimer> timer = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(EnergyControl.MODID, "timer"),
		new BlockEntityType<>(TileEntityTimer::new, Set.of(ModItems.timer)));

}
