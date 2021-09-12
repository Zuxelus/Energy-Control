package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.FluidInfo;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.multiblocks.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorCoolantTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorFuelTank;
import net.roguelogix.biggerreactors.multiblocks.reactor.simulation.IReactorSimulation;
import net.roguelogix.biggerreactors.multiblocks.reactor.tiles.ReactorBaseTile;
import net.roguelogix.biggerreactors.multiblocks.turbine.TurbineMultiblockController;
import net.roguelogix.biggerreactors.multiblocks.turbine.simulation.ITurbineFluidTank;
import net.roguelogix.biggerreactors.multiblocks.turbine.simulation.ITurbineSimulation;
import net.roguelogix.biggerreactors.multiblocks.turbine.tiles.TurbineBaseTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;

public class CrossBiggerReactors extends CrossModBase {

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof ReactorBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				ReactorMultiblockController controller = (ReactorMultiblockController) field.get(te);
				IReactorSimulation reactor = controller.simulation();

				CompoundNBT tag = new CompoundNBT();
				tag.putString("euType", "FE");
				tag.putDouble("storage", reactor.battery().stored());
				tag.putDouble("maxStorage", reactor.battery().capacity());
				return tag;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				TurbineMultiblockController controller = (TurbineMultiblockController) field.get(te);
				ITurbineSimulation turbine = controller.simulation();

				CompoundNBT tag = new CompoundNBT();
				tag.putString("euType", "FE");
				tag.putDouble("storage", turbine.battery().stored());
				tag.putDouble("maxStorage", turbine.battery().capacity());
				return tag;
			} catch (Throwable t) { }
		}
		return null;
	}

	@Override
	public int getReactorHeat(World world, BlockPos pos) {
		TileEntity te;
		for (Direction dir : Direction.values()) {
			te = world.getBlockEntity(pos.relative(dir));
			if (te instanceof ReactorBaseTile) {
				try {
					Field field = MultiblockTile.class.getDeclaredField("controller");
					field.setAccessible(true);
					ReactorMultiblockController controller = (ReactorMultiblockController) field.get(te);
					IReactorSimulation reactor = controller.simulation();
					return (int) reactor.caseHeat();
				} catch (Throwable t) { }
			}
		}
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof ReactorBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				ReactorMultiblockController controller = (ReactorMultiblockController) field.get(te);
				IReactorSimulation reactor = controller.simulation();
				if (reactor.isPassive())
					return null;

				IReactorCoolantTank tank = reactor.coolantTank();
				result.add(new FluidInfo(tank.getFluidInTank(0), tank.tankCapacity(0)));
				result.add(new FluidInfo(tank.getFluidInTank(1), tank.tankCapacity(1)));
				return result;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				TurbineMultiblockController controller = (TurbineMultiblockController) field.get(te);
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
	public CompoundNBT getCardData(TileEntity te) {
		if (te instanceof ReactorBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				ReactorMultiblockController controller = (ReactorMultiblockController) field.get(te);
				IReactorSimulation reactor = controller.simulation();

				CompoundNBT tag = new CompoundNBT();
				tag.putInt("type", 1);
				tag.putBoolean("reactorPoweredB", controller.isActive());
				tag.putBoolean("cooling", reactor.isPassive());
				tag.putDouble("heat", reactor.fuelHeat());
				tag.putDouble("coreHeat", reactor.caseHeat());
				tag.putString("storage", String.format("%s / %s", PanelString.getFormatter().format(reactor.battery().stored()), PanelString.getFormatter().format(reactor.battery().capacity())));
				tag.putDouble("output", reactor.outputLastTick());
				Vector3ic min = controller.minCoord(); 
				Vector3ic max = controller.maxCoord();
				tag.putInt("rods", controller.controlRodCount() * (max.y() - min.y() - 1));
				IReactorFuelTank tank = reactor.fuelTank();
				tag.putString("fuel", String.format("%s / %s", tank.fuel(), tank.capacity()));
				tag.putDouble("waste", tank.waste());
				tag.putDouble("consumption", reactor.fuelConsumptionLastTick());
				tag.putString("size", String.format("%sx%sx%s", max.x() - min.x() + 1, max.y() - min.y() + 1, max.z() - min.z() + 1));
				return tag;
			} catch (Throwable t) { }
		}
		if (te instanceof TurbineBaseTile) {
			try {
				Field field = MultiblockTile.class.getDeclaredField("controller");
				field.setAccessible(true);
				TurbineMultiblockController controller = (TurbineMultiblockController) field.get(te);
				ITurbineSimulation turbine = controller.simulation();

				CompoundNBT tag = new CompoundNBT();
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
