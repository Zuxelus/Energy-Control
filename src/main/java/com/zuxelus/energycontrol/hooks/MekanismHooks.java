package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.hooklib.asm.Hook;

import mekanism.common.tile.TileEntityEnergyCube;
import net.minecraft.tileentity.TileEntity;

public class MekanismHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void onUpdate(TileEntityEnergyCube te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Double> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, te.getEnergy());
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(te.getEnergy());
			map.put(te, values);
		}
	}
}
