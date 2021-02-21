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
	public static final RegistryObject<TileEntityType<TileEntityInfoPanel>> info_panel = TILE_ENTITY_TYPES.register("info_panel", () ->
		TileEntityType.Builder.create(TileEntityInfoPanel::new, ModItems.info_panel.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityInfoPanelExtender>> info_panel_extender = TILE_ENTITY_TYPES.register("info_panel_extender", () ->
		TileEntityType.Builder.create(TileEntityInfoPanelExtender::new, ModItems.info_panel_extender.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityAdvancedInfoPanel>> info_panel_advanced = TILE_ENTITY_TYPES.register("info_panel_advanced", () ->
		TileEntityType.Builder.create(TileEntityAdvancedInfoPanel::new, ModItems.info_panel_advanced.get()).build(null));
	public static final RegistryObject<TileEntityType<TileEntityAdvancedInfoPanelExtender>> info_panel_advanced_extender = TILE_ENTITY_TYPES.register("info_panel_advanced_extender", () ->
		TileEntityType.Builder.create(TileEntityAdvancedInfoPanelExtender::new, ModItems.info_panel_advanced_extender.get()).build(null));

}
