package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.hooklib.asm.Hook;

import cofh.redstoneflux.api.IEnergyStorage;
import cofh.thermalexpansion.block.storage.TileCell;
import net.minecraft.tileentity.TileEntity;

public class ThermalExpansionHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileCell te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		long value = 0;
		IEnergyStorage storage = ((TileCell) te).getEnergyStorage();
		if (storage != null) {
			value = storage.getEnergyStored();
		}

		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, value);
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(value);
			map.put(te, values);
		}
	}
}
