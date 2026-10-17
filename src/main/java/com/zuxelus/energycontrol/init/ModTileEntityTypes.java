package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModTileEntityTypes {
	public static final BlockEntityType<TileEntityHowlerAlarm> howler_alarm = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "howler_alarm"),
		BlockEntityType.Builder.of(TileEntityHowlerAlarm::new, ModItems.howler_alarm).build(null));
	public static final BlockEntityType<TileEntityIndustrialAlarm> industrial_alarm = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "industrial_alarm"),
		BlockEntityType.Builder.of(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm).build(null));
	public static final BlockEntityType<TileEntityThermalMonitor> thermal_monitor = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "thermal_monitor"),
		BlockEntityType.Builder.of(TileEntityThermalMonitor::new, ModItems.thermal_monitor).build(null));
	public static final BlockEntityType<TileEntityRangeTrigger> range_trigger = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "range_trigger"),
		BlockEntityType.Builder.of(TileEntityRangeTrigger::new, ModItems.range_trigger).build(null));
	public static final BlockEntityType<TileEntityRemoteThermalMonitor> remote_thermo = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "remote_thermo"),
		BlockEntityType.Builder.of(TileEntityRemoteThermalMonitor::new, ModItems.remote_thermo).build(null));
	public static final BlockEntityType<TileEntityInfoPanel> info_panel = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel"),
		BlockEntityType.Builder.of(TileEntityInfoPanel::new, ModItems.info_panel).build(null)); 
	public static final BlockEntityType<TileEntityInfoPanelExtender> info_panel_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_extender"),
		BlockEntityType.Builder.of(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanel> info_panel_advanced = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced"),
		BlockEntityType.Builder.of(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanelExtender> info_panel_advanced_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "info_panel_advanced_extender"),
		BlockEntityType.Builder.of(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender).build(null));
	public static final BlockEntityType<TileEntityHoloPanel> holo_panel = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel"),
		BlockEntityType.Builder.of(TileEntityHoloPanel::new, ModItems.holo_panel).build(null));
	public static final BlockEntityType<TileEntityHoloPanelExtender> holo_panel_extender = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "holo_panel_extender"),
		BlockEntityType.Builder.of(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender).build(null));
	//public static final BlockEntityType<TileEntityAverageCounter> average_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "average_counter"),
	//	BlockEntityType.Builder.create(TileEntityAverageCounter::new, ModItems.average_counter).build(null));
	//public static final BlockEntityType<TileEntityEnergyCounter> energy_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "energy_counter"),
	//	BlockEntityType.Builder.create(TileEntityEnergyCounter::new, ModItems.energy_counter).build(null));
	public static final BlockEntityType<TileEntityKitAssembler> kit_assembler = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "kit_assembler"),
		BlockEntityType.Builder.of(TileEntityKitAssembler::new, ModItems.kit_assembler).build(null));
	public static final BlockEntityType<TileEntityTimer> timer = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(EnergyControl.MODID, "timer"),
		BlockEntityType.Builder.of(TileEntityTimer::new, ModItems.timer).build(null));

}
