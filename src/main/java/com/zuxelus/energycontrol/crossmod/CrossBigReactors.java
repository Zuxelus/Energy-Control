package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import erogenousbeef.bigreactors.common.multiblock.IInputOutputPort;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorController;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineController;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CrossBigReactors extends CrossModBase {

	public CrossBigReactors() {
		super("bigreactors", null, null);
	}

	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (!modLoaded)
			return null;

		if (te instanceof TileEntityReactorController) {
			MultiblockReactor reactor = ((TileEntityReactorController) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			FluidTankInfo[] info = reactor.getCoolantContainer().getTankInfo(-1);
			List<IFluidTank> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidTank(tank.fluid, tank.capacity));
			return result;
		}
		if (te instanceof TileEntityTurbineController) {
			MultiblockTurbine turbine = ((TileEntityTurbineController) te).getTurbine();
			if (turbine == null)
				return null;

			IFluidHandler input = turbine.getFluidHandler(IInputOutputPort.Direction.Input);
			IFluidHandler output = turbine.getFluidHandler(IInputOutputPort.Direction.Output);
			List<IFluidTank> result = new ArrayList<>();
			result.add(new FluidTank(input.getTankProperties()[0].getContents(), input.getTankProperties()[0].getCapacity()));
			result.add(new FluidTank(output.getTankProperties()[0].getContents(), output.getTankProperties()[0].getCapacity()));
			return result;
		}
		return null;
	}
}
