package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.hooklib.asm.Hook;

import ic2.core.block.base.tile.TileEntityElectricBlock;
import ic2.core.block.generator.tile.TileEntityBasicSteamTurbine;
import net.minecraft.tileentity.TileEntity;

public class IC2ClassicHooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void update(TileEntityElectricBlock te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		ArrayList<Integer> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, te.getStoredEU());
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(te.getStoredEU());
			map.put(te, values);
		}
	}

	@Hook
	public static void update(TileEntityBasicSteamTurbine te) {
		if (!map.containsKey(te) || te.getWorld().isRemote)
			return;

		int output = (int) (te.energy >= 4000 ? 0 : 32D * te.speed + Math.min(64, te.tank.getFluidAmount()) / 8.0D);
		ArrayList<Integer> values = new ArrayList<>();
		values.add(output);
		map.put(te, values);
	}
}