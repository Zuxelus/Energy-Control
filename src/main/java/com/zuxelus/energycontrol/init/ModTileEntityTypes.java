package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.tileentities.*;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EnergyControl.MODID);

	public static final RegistryObject<TileEntityType<TileEntityHowlerAlarm>> howler_alarm = TILE_ENTITY_TYPES.register("howler_alarm", () ->
		TileEntityType.Builder.create(TileEntityHowlerAlarm::new, ModItems.howler_alarm.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityIndustrialAlarm>> industrial_alarm = TILE_ENTITY_TYPES.register("industrial_alarm", () ->
		TileEntityType.Builder.create(TileEntityIndustrialAlarm::new, ModItems.industrial_alarm.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityThermalMonitor>> thermal_monitor = TILE_ENTITY_TYPES.register("thermal_monitor", () ->
		TileEntityType.Builder.create(TileEntityThermalMonitor::new, ModItems.thermal_monitor.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityRangeTrigger>> range_trigger = TILE_ENTITY_TYPES.register("range_trigger", () ->
		TileEntityType.Builder.create(TileEntityRangeTrigger::new, ModItems.range_trigger.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityRemoteThermalMonitor>> remote_thermo = TILE_ENTITY_TYPES.register("remote_thermo", () ->
		TileEntityType.Builder.create(TileEntityRemoteThermalMonitor::new, ModItems.remote_thermo.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityInfoPanel>> info_panel = TILE_ENTITY_TYPES.register("info_panel", () ->
		TileEntityType.Builder.create(TileEntityInfoPanel::new, ModItems.info_panel.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityInfoPanelExtender>> info_panel_extender = TILE_ENTITY_TYPES.register("info_panel_extender", () ->
		TileEntityType.Builder.create(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityAdvancedInfoPanel>> info_panel_advanced = TILE_ENTITY_TYPES.register("info_panel_advanced", () ->
		TileEntityType.Builder.create(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityAdvancedInfoPanelExtender>> info_panel_advanced_extender = TILE_ENTITY_TYPES.register("info_panel_advanced_extender", () ->
		TileEntityType.Builder.create(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityHoloPanel>> holo_panel = TILE_ENTITY_TYPES.register("holo_panel", () ->
		TileEntityType.Builder.create(TileEntityHoloPanel::new, ModItems.holo_panel.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityHoloPanelExtender>> holo_panel_extender = TILE_ENTITY_TYPES.register("holo_panel_extender", () ->
		TileEntityType.Builder.create(TileEntityHoloPanelExtender::new, ModItems.holo_panel_extender.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityKitAssembler>> kit_assembler = TILE_ENTITY_TYPES.register("kit_assembler", () ->
		TileEntityType.Builder.create(TileEntityKitAssembler::new, ModItems.kit_assembler.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityTimer>> timer = TILE_ENTITY_TYPES.register("timer", () ->
		TileEntityType.Builder.create(TileEntityTimer::new, ModItems.timer.get()).build(null));

}
