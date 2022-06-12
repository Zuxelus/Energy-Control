package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.hooklib.asm.Hook;

import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule;
import net.minecraft.tileentity.TileEntity;

public class GalacticraftHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEntityEnergyStorageModule te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Double> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, (double) te.storage.getEnergyStoredGC());
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add((double) te.storage.getEnergyStoredGC());
			map.put(te, values);
		}
	}
}
