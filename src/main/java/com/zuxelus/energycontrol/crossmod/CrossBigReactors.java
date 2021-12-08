package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.zuxelus.energycontrol.api.PanelString;
import com.zuxelus.energycontrol.utils.FluidInfo;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.IFluidContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.AbstractReactorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.AbstractTurbineEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class CrossBigReactors extends CrossModBase {

	@Override
	public CompoundTag getEnergyData(BlockEntity te) {
		if (te instanceof AbstractReactorEntity) {
			Optional<MultiblockReactor> option = ((AbstractReactorEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockReactor reactor = option.get();
			if (reactor == null)
				return null;

			CompoundTag tag = new CompoundTag();
			tag.putString("euType", "FE");
			tag.putDouble("storage", reactor.getEnergyStored(EnergySystem.REFERENCE, null));
			tag.putDouble("maxStorage", reactor.getCapacity(EnergySystem.REFERENCE, null));
			return tag;
		}
		if (te instanceof AbstractTurbineEntity) {
			Optional<MultiblockTurbine> option = ((AbstractTurbineEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockTurbine turbine = option.get();
			if (turbine == null)
				return null;

			CompoundTag tag = new CompoundTag();
			tag.putString("euType", "FE");
			tag.putDouble("storage", turbine.getEnergyStored(EnergySystem.REFERENCE, null));
			tag.putDouble("maxStorage", turbine.getCapacity(EnergySystem.REFERENCE, null));
			return tag;
		}
		return null;
	}

	@Override
	public int getReactorHeat(Level world, BlockPos pos) {
		BlockEntity te;
		for (Direction dir : Direction.values()) {
			te = world.getBlockEntity(pos.relative(dir));
			if (te instanceof AbstractReactorEntity) {
				Optional<MultiblockReactor> option = ((AbstractReactorEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
				if (option.isPresent()) {
					MultiblockReactor reactor = option.get();
					if (reactor != null)
						return (int) reactor.getReactorHeat().getAsDouble();
				}
			}
		}
		return -1;
	}

	@Override
	public List<FluidInfo> getAllTanks(BlockEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof AbstractReactorEntity) {
			Optional<MultiblockReactor> option = ((AbstractReactorEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockReactor reactor = option.get();
			if (reactor == null)
				return null;
			if (reactor.getOperationalMode().isPassive())
				return null;
			IFluidContainer tank = reactor.getFluidContainer();
			FluidStack stack = tank.getWrapper(IoDirection.Input).getFluidInTank(1);
			result.add(new FluidInfo(stack, tank.getCapacity()));
			stack = tank.getWrapper(IoDirection.Output).getFluidInTank(1);
			result.add(new FluidInfo(stack, tank.getCapacity()));
			return result;
		}
		if (te instanceof AbstractTurbineEntity) {
			Optional<MultiblockTurbine> option = ((AbstractTurbineEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockTurbine turbine = option.get();
			if (turbine == null)
				return null;

			IFluidContainer tank = turbine.getFluidContainer();
			FluidStack stack = tank.getWrapper(IoDirection.Input).getFluidInTank(1);
			result.add(new FluidInfo(stack, tank.getCapacity()));
			stack = tank.getWrapper(IoDirection.Output).getFluidInTank(1);
			result.add(new FluidInfo(stack, tank.getCapacity()));
			return result;
		}
		return null;
	}

	@Override
	public CompoundTag getCardData(BlockEntity te) {
		if (te instanceof AbstractReactorEntity) {
			Optional<MultiblockReactor> option = ((AbstractReactorEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockReactor reactor = option.get();
			if (reactor == null)
				return null;

			CompoundTag tag = new CompoundTag();
			tag.putInt("type", 1);
			tag.putBoolean("reactorPoweredB", reactor.isMachineActive());
			tag.putBoolean("cooling", reactor.getOperationalMode().isPassive());
			tag.putDouble("heat", reactor.getFuelHeat().getAsDouble());
			tag.putDouble("coreHeat", reactor.getReactorHeat().getAsDouble());
			tag.putString("storage", String.format("%s / %s", PanelString.getFormatter().format(reactor.getEnergyStored(EnergySystem.REFERENCE, null)), PanelString.getFormatter().format(reactor.getCapacity(EnergySystem.REFERENCE, null))));
			tag.putDouble("output", reactor.getUiStats().getAmountGeneratedLastTick());
			tag.putInt("rods", reactor.getFuelRodsCount());
			tag.putString("fuel", String.format("%s / %s", reactor.getFuelAmount(), reactor.getCapacity()));
			tag.putDouble("waste", reactor.getWasteAmount());
			tag.putDouble("consumption", reactor.getUiStats().getFuelConsumedLastTick());
			CuboidBoundingBox box = reactor.getBoundingBox();
			tag.putString("size", String.format("%sx%sx%s", box.getLengthX() + 1, box.getLengthY() + 1, box.getLengthZ() + 1));
			return tag;
		}
		if (te instanceof AbstractTurbineEntity) {
			Optional<MultiblockTurbine> option = ((AbstractTurbineEntity)te).getMultiblockController().filter(AbstractMultiblockController::isAssembled);
			if (!option.isPresent())
				return null;
			MultiblockTurbine turbine = option.get();
			if (turbine == null)
				return null;

			CompoundTag tag = new CompoundTag();
			tag.putInt("type", 2);
			tag.putBoolean("reactorPoweredB", turbine.isMachineActive());
			tag.putString("storage", String.format("%s / %s", PanelString.getFormatter().format(turbine.getEnergyStored(EnergySystem.REFERENCE, null)), PanelString.getFormatter().format(turbine.getCapacity(EnergySystem.REFERENCE, null))));
			tag.putDouble("output", turbine.getEnergyGeneratedLastTick());
			tag.putDouble("speed", turbine.getRotorSpeed());
			//tag.putDouble("speedMax", turbine.getMaxRotorSpeed());
			tag.putDouble("efficiency", turbine.getRotorEfficiencyLastTick() * 100);
			tag.putDouble("consumption", turbine.getMaxIntakeRate());
			tag.putInt("blades", turbine.getRotorBladesCount());
			tag.putInt("mass", turbine.getRotorMass());
			CuboidBoundingBox box = turbine.getBoundingBox();
			tag.putString("size", String.format("%sx%sx%s", box.getLengthX() + 1, box.getLengthY() + 1, box.getLengthZ() + 1));
			return tag;
		}
		return null;
	}
}
