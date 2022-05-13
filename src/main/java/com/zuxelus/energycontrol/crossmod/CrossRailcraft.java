package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mods.railcraft.common.blocks.machine.beta.TileTankIronGauge;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelGauge;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossRailcraft extends CrossModBase {

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileTankIronGauge) {
			result.add(new FluidInfo(((TileTankIronGauge) te).getTank()));
			return result;
		}
		if (te instanceof TileTankSteelGauge) {
			result.add(new FluidInfo(((TileTankSteelGauge) te).getTank()));
			return result;
		}
		return null;
	}
}
