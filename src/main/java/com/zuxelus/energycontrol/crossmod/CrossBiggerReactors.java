package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.roguelogix.biggerreactors.multiblocks.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorSimulation;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorSimulation.ICoolantTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorSimulation.IFuelTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.tiles.ReactorBaseTile;
import net.roguelogix.biggerreactors.multiblocks.reactor.util.ReactorTransitionTank;
import net.roguelogix.biggerreactors.multiblocks.turbine.TurbineMultiblockController;
import net.roguelogix.biggerreactors.multiblocks.turbine.simulation.ITurbineFluidTank;
import net.roguelogix.biggerreactors.multiblocks.turbine.simulation.ITurbineSimulation;
import net.roguelogix.biggerreactors.multiblocks.turbine.tiles.TurbineBaseTile;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;

public class CrossBiggerReactors extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		if (te instanceof ReactorBaseTile) {
			try {
				ReactorMultiblockController controller = ((ReactorBaseTile) te).controller();
				IReactorSimulation reactor = controller.simulation();

				CompoundTag tag = new CompoundTag();
				tag.putString("euType", "FE");
				tag.putDouble("storage", reactor.battery().stored());
				tag.putDouble("maxStorage", reactor.battery().capacity());
				return tag;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				TurbineMultiblockController controller = ((TurbineBaseTile) te).controller();
				ITurbineSimulation turbine = controller.simulation();

				CompoundTag tag = new CompoundTag();
				tag.putString("euType", "FE");
				tag.putDouble("storage", turbine.battery().stored());
				tag.putDouble("maxStorage", turbine.battery().capacity());
				return tag;
			} catch (Throwable t) { }
		}
		return null;
	}

	@Override
	public int getReactorHeat(Level world, BlockPos pos) {
		BlockEntity te;
		for (Direction dir : Direction.values()) {
			te = world.getBlockEntity(pos.relative(dir));
			if (te instanceof ReactorBaseTile) {
				try {
					ReactorMultiblockController controller = ((ReactorBaseTile) te).controller();
					IReactorSimulation reactor = controller.simulation();
					return (int) reactor.stackHeat();
				} catch (Throwable t) { }
			}
		}
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof ReactorBaseTile) {
			try {
				ReactorMultiblockController controller = ((ReactorBaseTile) te).controller();
				IReactorSimulation reactor = controller.simulation();
				if (reactor.battery() != null)
					return null;

				ReactorTransitionTank tank = controller.coolantTank();
				ICoolantTank tank2 = reactor.coolantTank();
				if (tank != null && tank2 != null) {
					result.add(new FluidInfo(tank.fluidTypeInTank(0), tank2.liquidAmount(), tank2.perSideCapacity()));
					result.add(new FluidInfo(tank.fluidTypeInTank(1), tank2.vaporAmount(), tank2.perSideCapacity()));
				}
				return result;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				TurbineMultiblockController controller = ((TurbineBaseTile) te).controller();
				ITurbineSimulation turbine = controller.simulation();

				ITurbineFluidTank tank = turbine.fluidTank();
				result.add(new FluidInfo(tank.getFluidInTank(0), tank.tankCapacity(0)));
				result.add(new FluidInfo(tank.getFluidInTank(1), tank.tankCapacity(1)));
				return result;
			} catch (Throwable t) { }
		}
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		if (te instanceof ReactorBaseTile) {
			try {
				ReactorMultiblockController controller = ((ReactorBaseTile) te).controller();
				IReactorSimulation reactor = controller.simulation();

				CompoundTag tag = new CompoundTag();
				tag.putInt("type", 1);
				tag.putBoolean("reactorPoweredB", controller.isActive());
				tag.putBoolean("cooling", reactor.battery() != null);
				tag.putDouble("heat", reactor.fuelHeat());
				tag.putDouble("coreHeat", reactor.stackHeat());
				if (reactor.battery() != null)
					tag.putString("storage", String.format("%s / %s", PanelString.getFormatter().format(reactor.battery().stored()), PanelString.getFormatter().format(reactor.battery().capacity())));
				tag.putDouble("output", (reactor.battery() != null) ? reactor.battery().generatedLastTick() : reactor.coolantTank().transitionedLastTick());
				Vector3ic min = controller.minCoord();
				Vector3ic max = controller.maxCoord();
				tag.putInt("rods", controller.controlRodCount() * (max.y() - min.y() - 1));
				IFuelTank tank = reactor.fuelTank();
				tag.putString("fuel", String.format("%s / %s", tank.fuel(), tank.capacity()));
				tag.putDouble("waste", tank.waste());
				tag.putDouble("consumption", reactor.fuelTank().burnedLastTick());
				tag.putString("size", String.format("%sx%sx%s", max.x() - min.x() + 1, max.y() - min.y() + 1, max.z() - min.z() + 1));
				return tag;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				TurbineMultiblockController controller = ((TurbineBaseTile) te).controller();
				ITurbineSimulation turbine = controller.simulation();

				CompoundTag tag = new CompoundTag();
				tag.putInt("type", 2);
				tag.putBoolean("reactorPoweredB", turbine.active());
				tag.putString("storage", String.format("%s / %s", PanelString.getFormatter().format(turbine.battery().stored()), PanelString.getFormatter().format(turbine.battery().capacity())));
				tag.putDouble("output", turbine.FEGeneratedLastTick());
				tag.putDouble("speed", turbine.RPM());
				tag.putDouble("efficiency", turbine.bladeEfficiencyLastTick() * 100);
				tag.putDouble("consumption", turbine.nominalFlowRate());
				tag.putInt("blades", 0); // TODO
				tag.putInt("mass", (int) turbine.rotorMass());
				Vector3ic min = controller.minCoord();
				Vector3ic max = controller.maxCoord();
				tag.putString("size", String.format("%sx%sx%s", max.x() - min.x() + 1, max.y() - min.y() + 1, max.z() - min.z() + 1));
				return tag;
			} catch (Throwable t) { }
		}
		return null;
	}
}
