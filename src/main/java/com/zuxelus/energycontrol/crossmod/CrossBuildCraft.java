package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.utils.FluidInfo;

import buildcraft.api.transport.pipe.PipeFlow;
import buildcraft.factory.tile.TileTank;
import buildcraft.lib.fluid.Tank;
import buildcraft.lib.fluid.TankManager;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.transport.pipe.Pipe;
import buildcraft.transport.pipe.flow.PipeFlowFluids;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CrossBuildCraft extends CrossModBase {

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		try {
			if (te instanceof TilePipeHolder) {
				Pipe pipe = ((TilePipeHolder) te).getPipe();
				if (pipe == null)
					return null;
				PipeFlow flow = pipe.getFlow();
				if (flow instanceof PipeFlowFluids) {
					Field field = PipeFlowFluids.class.getDeclaredField("currentFluid");
					field.setAccessible(true);
					FluidStack stack = (FluidStack) field.get(flow);
					if (stack == null) {
						List<FluidInfo> result = new ArrayList<>();
						result.add(new FluidInfo(stack, ((PipeFlowFluids) flow).capacity));
						return result;
					}
					int amount = 0;
					for (EnumFacing side : EnumFacing.VALUES) {
						FluidStack currStack = ((PipeFlowFluids) flow).extractFluidsForce(0, 100000, side, true);
						amount += currStack.amount;
					}
					List<FluidInfo> result = new ArrayList<>();
					result.add(new FluidInfo(stack.getFluid(), stack.getLocalizedName(), amount, ((PipeFlowFluids) flow).capacity));
					return result;
				}
			}
			if (te instanceof TileTank) {
				IFluidHandler fluid = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				if (fluid != null) {
					IFluidTankProperties[] tanks = fluid.getTankProperties();
					List<FluidInfo> result = new ArrayList<>();
					for (IFluidTankProperties tank : tanks)
						result.add(new FluidInfo(tank.getContents(), tank.getCapacity()));
					return result;
				}
			}
			if (te instanceof TileBC_Neptune) {
				Field field = TileBC_Neptune.class.getDeclaredField("tankManager");
				field.setAccessible(true);
				TankManager tankManager = (TankManager) field.get(te);
				IFluidTankProperties[] tanks = tankManager.getTankProperties();
				if (tanks.length > 0) {
					List<FluidInfo> result = new ArrayList<>();
					for (IFluidTankProperties tank : tanks)
						result.add(new FluidInfo(tank.getContents(), tank.getCapacity()));
					return result;
				}
			}
		} catch (Throwable t) { }
		return null;
	}

	/*@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (!(te instanceof TileEngineBase_BC8))
			return null;
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("type", 1);
		tag.setDouble("output",(double) ((TileEngineBase_BC8) te).getCurrentOutput() / Math.pow(10, 6));
		//tag.setBoolean("powered",((TileEngineBase_BC8) te).isRedstonePowered);
		tag.setBoolean("active",((TileEngineBase_BC8) te).isBurning());
		tag.setDouble("power", ((TileEngineBase_BC8) te).getEnergyStored() / Math.pow(10, 6));
		tag.setDouble("powerLevel", ((TileEngineBase_BC8) te).getPowerLevel() * 100);
		tag.setDouble("maxPower", ((TileEngineBase_BC8) te).getMaxPower() / Math.pow(10, 6));
		tag.setDouble("heat", ((TileEngineBase_BC8) te).getHeat());
		tag.setDouble("heatLevel", ((TileEngineBase_BC8) te).getHeatLevel() * 100);
		tag.setDouble("speed", ((TileEngineBase_BC8) te).getPistonSpeed());
		return tag;
	}*/
}
