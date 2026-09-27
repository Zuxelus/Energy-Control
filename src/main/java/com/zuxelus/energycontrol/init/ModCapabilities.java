package com.zuxelus.energycontrol.init;

import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities {

	public static void register(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.info_panel.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.info_panel_advanced.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.holo_panel.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.range_trigger.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.thermal_monitor.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.remote_thermo.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Item.BLOCK, ModTileEntityTypes.kit_assembler.get(), TileEntityInventory::getItemHandler);
		event.registerBlockEntity(Capabilities.Energy.BLOCK, ModTileEntityTypes.kit_assembler.get(), (be, side) -> be.getEnergyHandler(side));
	}
}
