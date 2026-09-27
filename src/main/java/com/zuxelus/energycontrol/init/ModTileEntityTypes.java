package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModTileEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EnergyControl.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityHowlerAlarm>> howler_alarm = TILE_ENTITY_TYPES.register("howler_alarm", () ->
		new BlockEntityType<>(TileEntityHowlerAlarm::new, ModItems.howler_alarm.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityIndustrialAlarm>> industrial_alarm = TILE_ENTITY_TYPES.register("industrial_alarm", () ->
		new BlockEntityType<>(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityThermalMonitor>> thermal_monitor = TILE_ENTITY_TYPES.register("thermal_monitor", () ->
		new BlockEntityType<>(TileEntityThermalMonitor::new, ModItems.thermal_monitor.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityRangeTrigger>> range_trigger = TILE_ENTITY_TYPES.register("range_trigger", () ->
		new BlockEntityType<>(TileEntityRangeTrigger::new, ModItems.range_trigger.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityRemoteThermalMonitor>> remote_thermo = TILE_ENTITY_TYPES.register("remote_thermo", () ->
		new BlockEntityType<>(TileEntityRemoteThermalMonitor::new, ModItems.remote_thermo.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityInfoPanel>> info_panel = TILE_ENTITY_TYPES.register("info_panel", () ->
		new BlockEntityType<>(TileEntityInfoPanel::new, ModItems.info_panel.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityInfoPanelExtender>> info_panel_extender = TILE_ENTITY_TYPES.register("info_panel_extender", () ->
		new BlockEntityType<>(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityAdvancedInfoPanel>> info_panel_advanced = TILE_ENTITY_TYPES.register("info_panel_advanced", () ->
		new BlockEntityType<>(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityAdvancedInfoPanelExtender>> info_panel_advanced_extender = TILE_ENTITY_TYPES.register("info_panel_advanced_extender", () ->
		new BlockEntityType<>(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityHoloPanel>> holo_panel = TILE_ENTITY_TYPES.register("holo_panel", () ->
		new BlockEntityType<>(TileEntityHoloPanel::new, ModItems.holo_panel.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityHoloPanelExtender>> holo_panel_extender = TILE_ENTITY_TYPES.register("holo_panel_extender", () ->
		new BlockEntityType<>(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender.get()));
	/*public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityAverageCounter>> average_counter = TILE_ENTITY_TYPES.register("average_counter", () ->
		new BlockEntityType<>(TileEntityAverageCounter::new, ModItems.average_counter.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityEnergyCounter>> energy_counter = TILE_ENTITY_TYPES.register("energy_counter", () ->
		new BlockEntityType<>(TileEntityEnergyCounter::new, ModItems.energy_counter.get()));*/
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityKitAssembler>> kit_assembler = TILE_ENTITY_TYPES.register("kit_assembler", () ->
		new BlockEntityType<>(TileEntityKitAssembler::new, ModItems.kit_assembler.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityTimer>> timer = TILE_ENTITY_TYPES.register("timer", () ->
		new BlockEntityType<>(TileEntityTimer::new, ModItems.timer.get()));

}
