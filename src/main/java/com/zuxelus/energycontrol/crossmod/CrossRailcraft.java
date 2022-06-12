package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.zuxelus.energycontrol.utils.FluidInfo;

import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.FluidLogic;
import mods.railcraft.common.fluids.TankManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

public class CrossRailcraft extends CrossModBase {

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof TileLogic) {
			Optional<FluidLogic> logic = ((TileLogic) te).getLogic(FluidLogic.class);
			if (!logic.isPresent())
				return null;
			TankManager manager = logic.get().getTankManager();
			if (manager == null)
				return null;
			FluidTank tank = manager.get(0);
			if (tank == null)
				return null;
			result.add(new FluidInfo(tank));
			return result;
		}
		return null;
	}
}
