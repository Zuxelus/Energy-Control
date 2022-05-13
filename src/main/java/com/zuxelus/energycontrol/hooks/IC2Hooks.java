package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.hooklib.asm.Hook;

import ic2.core.block.comp.Energy;
import ic2.core.block.kineticgenerator.tileentity.TileEntityElectricKineticGenerator;
import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.tileentity.TileEntity;

public class IC2Hooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void updateEntityServer(TileEntityElectricBlock te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		ArrayList<Double> values = map.get(te);
		if (values != null && values.size() > 0) {
			for (int i = 20; i > 0; i--)
				values.set(i, values.get(i - 1));
			values.set(0, te.energy);
		} else {
			values = new ArrayList<>();
			for (int i = 0; i < 21; i++)
				values.add(te.energy);
			map.put(te, values);
		}
	}

	@Hook
	public static void updateEntityServer(TileEntityElectricKineticGenerator te) {
		if (!map.containsKey(te) || te.getWorldObj().isRemote)
			return;

		double output = 0.0D;
		if (1000.0D - te.ku > 1.0D) {
			Energy energy = (Energy) te.getComponent("energy");
			output = Math.min(1000.0D - te.ku, energy.getEnergy() * DataHelper.getFloat(TileEntityElectricKineticGenerator.class, "kuPerEU", te));
		}
		ArrayList<Double> values = new ArrayList<>();
		values.add(output);
		map.put(te, values);
	}
}