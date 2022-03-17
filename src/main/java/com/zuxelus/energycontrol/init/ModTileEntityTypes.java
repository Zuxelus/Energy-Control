package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, EnergyControl.MODID);

	public static final RegistryObject<BlockEntityType<TileEntityHowlerAlarm>> howler_alarm = TILE_ENTITY_TYPES.register("howler_alarm", () ->
		BlockEntityType.Builder.of(TileEntityHowlerAlarm::new, ModItems.howler_alarm.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityIndustrialAlarm>> industrial_alarm = TILE_ENTITY_TYPES.register("industrial_alarm", () ->
		BlockEntityType.Builder.of(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityThermalMonitor>> thermal_monitor = TILE_ENTITY_TYPES.register("thermal_monitor", () ->
		BlockEntityType.Builder.of(TileEntityThermalMonitor::new, ModItems.thermal_monitor.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityRangeTrigger>> range_trigger = TILE_ENTITY_TYPES.register("range_trigger", () ->
		BlockEntityType.Builder.of(TileEntityRangeTrigger::new, ModItems.range_trigger.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityRemoteThermalMonitor>> remote_thermo = TILE_ENTITY_TYPES.register("remote_thermo", () ->
		BlockEntityType.Builder.of(TileEntityRemoteThermalMonitor::new, ModItems.remote_thermo.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityInfoPanel>> info_panel = TILE_ENTITY_TYPES.register("info_panel", () ->
		BlockEntityType.Builder.of(TileEntityInfoPanel::new, ModItems.info_panel.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityInfoPanelExtender>> info_panel_extender = TILE_ENTITY_TYPES.register("info_panel_extender", () ->
		BlockEntityType.Builder.of(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityAdvancedInfoPanel>> info_panel_advanced = TILE_ENTITY_TYPES.register("info_panel_advanced", () ->
		BlockEntityType.Builder.of(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityAdvancedInfoPanelExtender>> info_panel_advanced_extender = TILE_ENTITY_TYPES.register("info_panel_advanced_extender", () ->
		BlockEntityType.Builder.of(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityHoloPanel>> holo_panel = TILE_ENTITY_TYPES.register("holo_panel", () ->
		BlockEntityType.Builder.of(TileEntityHoloPanel::new, ModItems.holo_panel.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityHoloPanelExtender>> holo_panel_extender = TILE_ENTITY_TYPES.register("holo_panel_extender", () ->
		BlockEntityType.Builder.of(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender.get()).build(null));
	//public static final RegistryObject<BlockEntityType<TileEntityAverageCounter>> average_counter = TILE_ENTITY_TYPES.register("average_counter", () ->
	//	BlockEntityType.Builder.of(TileEntityAverageCounter::new, ModItems.average_counter.get()).build(null));
	//public static final RegistryObject<BlockEntityType<TileEntityEnergyCounter>> energy_counter = TILE_ENTITY_TYPES.register("energy_counter", () ->
	//	BlockEntityType.Builder.of(TileEntityEnergyCounter::new, ModItems.energy_counter.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityKitAssembler>> kit_assembler = TILE_ENTITY_TYPES.register("kit_assembler", () ->
		BlockEntityType.Builder.of(TileEntityKitAssembler::new, ModItems.kit_assembler.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileEntityTimer>> timer = TILE_ENTITY_TYPES.register("timer", () ->
		BlockEntityType.Builder.of(TileEntityTimer::new, ModItems.timer.get()).build(null));

}
