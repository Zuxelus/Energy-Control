package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyStorageCore;
import com.zuxelus.hooklib.asm.Hook;

import net.minecraft.tileentity.TileEntity;

public class DraconicEvolutionHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEnergyStorageCore te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, te.getExtendedStorage());
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(te.getExtendedStorage());
			map.put(te, values);
		}
	}
}
