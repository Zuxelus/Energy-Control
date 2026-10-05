package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModTileEntityTypes {
	public static final BlockEntityType<TileEntityHowlerAlarm> howler_alarm = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "howler_alarm"),
		BlockEntityType.Builder.create(TileEntityHowlerAlarm::new, ModItems.howler_alarm).build(null));
	public static final BlockEntityType<TileEntityIndustrialAlarm> industrial_alarm = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "industrial_alarm"),
		BlockEntityType.Builder.create(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm).build(null));
	public static final BlockEntityType<TileEntityThermalMonitor> thermal_monitor = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "thermal_monitor"),
		BlockEntityType.Builder.create(TileEntityThermalMonitor::new, ModItems.thermal_monitor).build(null));
	public static final BlockEntityType<TileEntityRangeTrigger> range_trigger = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "range_trigger"),
		BlockEntityType.Builder.create(TileEntityRangeTrigger::new, ModItems.range_trigger).build(null));
	public static final BlockEntityType<TileEntityRemoteThermalMonitor> remote_thermo = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "remote_thermo"),
		BlockEntityType.Builder.create(TileEntityRemoteThermalMonitor::new, ModItems.remote_thermo).build(null));
	public static final BlockEntityType<TileEntityInfoPanel> info_panel = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "info_panel"),
		BlockEntityType.Builder.create(TileEntityInfoPanel::new, ModItems.info_panel).build(null)); 
	public static final BlockEntityType<TileEntityInfoPanelExtender> info_panel_extender = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "info_panel_extender"),
		BlockEntityType.Builder.create(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanel> info_panel_advanced = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "info_panel_advanced"),
		BlockEntityType.Builder.create(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanelExtender> info_panel_advanced_extender = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "info_panel_advanced_extender"),
		BlockEntityType.Builder.create(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender).build(null));
	public static final BlockEntityType<TileEntityHoloPanel> holo_panel = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "holo_panel"),
		BlockEntityType.Builder.create(TileEntityHoloPanel::new, ModItems.holo_panel).build(null));
	public static final BlockEntityType<TileEntityHoloPanelExtender> holo_panel_extender = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "holo_panel_extender"),
		BlockEntityType.Builder.create(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender).build(null));
	//public static final BlockEntityType<TileEntityAverageCounter> average_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "average_counter"),
	//	BlockEntityType.Builder.create(TileEntityAverageCounter::new, ModItems.average_counter).build(null));
	//public static final BlockEntityType<TileEntityEnergyCounter> energy_counter = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "energy_counter"),
	//	BlockEntityType.Builder.create(TileEntityEnergyCounter::new, ModItems.energy_counter).build(null));
	public static final BlockEntityType<TileEntityKitAssembler> kit_assembler = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "kit_assembler"),
		BlockEntityType.Builder.create(TileEntityKitAssembler::new, ModItems.kit_assembler).build(null));
	public static final BlockEntityType<TileEntityTimer> timer = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(EnergyControl.MODID, "timer"),
		BlockEntityType.Builder.create(TileEntityTimer::new, ModItems.timer).build(null));

}
