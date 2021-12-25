package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.tileentities.*;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class ModTileEntityTypes {
	public static final BlockEntityType<TileEntityHowlerAlarm> howler_alarm = Registry.register(Registry.BLOCK_ENTITY_TYPE, "howler_alarm",
		FabricBlockEntityTypeBuilder.create(TileEntityHowlerAlarm::new, ModItems.howler_alarm).build(null));
	public static final BlockEntityType<TileEntityIndustrialAlarm> industrial_alarm = Registry.register(Registry.BLOCK_ENTITY_TYPE, "industrial_alarm",
		FabricBlockEntityTypeBuilder.create(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm).build(null));
	public static final BlockEntityType<TileEntityThermalMonitor> thermal_monitor = Registry.register(Registry.BLOCK_ENTITY_TYPE, "thermal_monitor",
		FabricBlockEntityTypeBuilder.create(TileEntityThermalMonitor::new, ModItems.thermal_monitor).build(null));
	public static final BlockEntityType<TileEntityRangeTrigger> range_trigger = Registry.register(Registry.BLOCK_ENTITY_TYPE, "range_trigger",
		FabricBlockEntityTypeBuilder.create(TileEntityRangeTrigger::new, ModItems.range_trigger).build(null));
	public static final BlockEntityType<TileEntityInfoPanel> info_panel = Registry.register(Registry.BLOCK_ENTITY_TYPE, "info_panel",
		FabricBlockEntityTypeBuilder.create(TileEntityInfoPanel::new, ModItems.info_panel).build(null)); 
	public static final BlockEntityType<TileEntityInfoPanelExtender> info_panel_extender = Registry.register(Registry.BLOCK_ENTITY_TYPE, "info_panel_extender",
		FabricBlockEntityTypeBuilder.create(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanel> info_panel_advanced = Registry.register(Registry.BLOCK_ENTITY_TYPE, "info_panel_advanced",
		FabricBlockEntityTypeBuilder.create(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced).build(null));
	public static final BlockEntityType<TileEntityAdvancedInfoPanelExtender> info_panel_advanced_extender = Registry.register(Registry.BLOCK_ENTITY_TYPE, "info_panel_advanced_extender",
		FabricBlockEntityTypeBuilder.create(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender).build(null));
	public static final BlockEntityType<TileEntityHoloPanel> holo_panel = Registry.register(Registry.BLOCK_ENTITY_TYPE, "holo_panel",
		FabricBlockEntityTypeBuilder.create(TileEntityHoloPanel::new, ModItems.holo_panel).build(null));
	public static final BlockEntityType<TileEntityHoloPanelExtender> holo_panel_extender = Registry.register(Registry.BLOCK_ENTITY_TYPE, "holo_panel_extender",
		FabricBlockEntityTypeBuilder.create(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender).build(null));
	//public static final BlockEntityType<TileEntityAverageCounter> average_counter = Registry.register(Registry.BLOCK_ENTITY_TYPE, "average_counter",
	//	FabricBlockEntityTypeBuilder.create(TileEntityAverageCounter::new, ModItems.average_counter).build(null));
	//public static final BlockEntityType<TileEntityEnergyCounter> energy_counter = Registry.register(Registry.BLOCK_ENTITY_TYPE, "energy_counter",
	//	FabricBlockEntityTypeBuilder.create(TileEntityEnergyCounter::new, ModItems.energy_counter).build(null));
	public static final BlockEntityType<TileEntityKitAssembler> kit_assembler = Registry.register(Registry.BLOCK_ENTITY_TYPE, "kit_assembler",
		FabricBlockEntityTypeBuilder.create(TileEntityKitAssembler::new, ModItems.kit_assembler).build(null));
	public static final BlockEntityType<TileEntityTimer> timer = Registry.register(Registry.BLOCK_ENTITY_TYPE, "timer",
		FabricBlockEntityTypeBuilder.create(TileEntityTimer::new, ModItems.timer).build(null));

}
