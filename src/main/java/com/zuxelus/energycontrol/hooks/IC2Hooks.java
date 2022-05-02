package com.zuxelus.energycontrol.hooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hbm.tileentity.machine.*;
import com.hbm.tileentity.machine.rbmk.RBMKDials;
import com.hbm.tileentity.machine.rbmk.TileEntityRBMKBoiler;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.hooklib.asm.Hook;

import ic2.core.block.comp.Energy;
import ic2.core.block.kineticgenerator.tileentity.TileEntityElectricKineticGenerator;
import net.minecraft.tileentity.TileEntity;

public class IC2Hooks {
	public static Map<TileEntity, ArrayList> map = new HashMap<TileEntity, ArrayList>();

	@Hook
	public static void updateEntityServer(TileEntityElectricKineticGenerator te) {
		if (te.getWorldObj().isRemote)
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