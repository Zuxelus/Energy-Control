package com.zuxelus.energycontrol.crossmod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import buildcraft.api.transport.pipe.PipeFlow;
import buildcraft.api.core.EnumPipePart;
import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.fluid.TankManager;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.transport.pipe.Pipe;
import buildcraft.transport.pipe.flow.PipeFlowFluids;
import buildcraft.transport.tile.TilePipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class CrossBuildCraft extends CrossModBase {

	@Override
	public ItemStack getGeneratorCard(TileEntity te) {
		if (te instanceof TileEngineBase_BC8) {
			ItemStack card = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_ENGINE);
			ItemStackHelper.setCoordinates(card, te.getPos());
			return card;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TilePipeHolder) {
			Pipe pipe = ((TilePipeHolder) te).getPipe();
			if (pipe != null) {
				PipeFlow flow = pipe.getFlow();
				if (flow instanceof PipeFlowFluids) {
					try {
						Field field = PipeFlowFluids.class.getDeclaredField("currentFluid");
						field.setAccessible(true);
						FluidStack stack = (FluidStack) field.get(flow);
						if (stack == null) {
							List<IFluidTank> result = new ArrayList<>();
							result.add(new FluidTank(stack, ((PipeFlowFluids) flow).capacity));
							return result;
						}
						int amount = 0;
						for (EnumFacing side : EnumFacing.VALUES) {
							FluidStack currStack = ((PipeFlowFluids) flow).extractFluidsForce(0, 100000, side, true);
							amount += currStack.amount;
						}
						List<IFluidTank> result = new ArrayList<>();
						result.add(new FluidTank(stack.getFluid(), amount, ((PipeFlowFluids) flow).capacity));
						return result;
					} catch (Throwable t) {
						return null;
					}
				}
			}
		}
		if (te instanceof TileBC_Neptune) {
			try {
				Field field = TileBC_Neptune.class.getDeclaredField("tankManager");
				field.setAccessible(true);
				TankManager tankManager = (TankManager) field.get(te);
				IFluidTankProperties[] tanks = tankManager.getTankProperties();
				if (tanks.length > 0) {
					List<IFluidTank> result = new ArrayList<>();
					for (IFluidTankProperties tank : tanks)
						result.add(new FluidTank(tank.getContents(), tank.getCapacity()));
					return result;
				}
			} catch (Throwable t) {
				return null;
			}
		}
		return null;
	}
}
