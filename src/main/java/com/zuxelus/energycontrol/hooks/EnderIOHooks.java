package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.hooklib.asm.Hook;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.tileentity.TileEntity;

public class EnderIOHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void doUpdate(TileCapBank te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		ArrayList<Long> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, (long) te.getEnergyStored());
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add((long) te.getEnergyStored());
			map.put(te, values);
		}
	}
}
