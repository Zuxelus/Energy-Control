package com.zuxelus.energycontrol.init;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import team.reborn.energy.api.EnergyStorage;

public class ModCapabilities {

	public static void register() {
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.info_panel);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.info_panel_advanced);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.holo_panel);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.range_trigger);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.thermal_monitor);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.remote_thermo);
		ItemStorage.SIDED.registerForBlockEntity(TileEntityInventory::getItemStorage, ModTileEntityTypes.kit_assembler);
		EnergyStorage.SIDED.registerForBlockEntity(TileEntityKitAssembler::getEnergyInput, ModTileEntityTypes.kit_assembler);
	}
}
