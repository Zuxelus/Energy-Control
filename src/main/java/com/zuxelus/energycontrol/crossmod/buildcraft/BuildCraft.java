package com.zuxelus.energycontrol.crossmod.buildcraft;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import buildcraft.lib.engine.TileEngineBase_BC8;
import buildcraft.lib.fluid.TankManager;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class BuildCraft extends CrossBC {

	@Override
	public boolean modLoaded() {
		return true;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileBC_Neptune) {
			try {
				Field field = TileBC_Neptune.class.getDeclaredField("tankManager");
				field.setAccessible(true);
				TankManager tankManager = (TankManager) field.get(te);
				IFluidTankProperties[] tanks = tankManager.getTankProperties();
				List<IFluidTank> result = new ArrayList<>();
				for (IFluidTankProperties tank : tanks)
					result.add(new FluidTank(tank.getContents(), tank.getCapacity()));
				return result;
			} catch (Throwable t) {
				return null;
			}
		}

		return null;
	}

	@Override
	public ItemStack getGeneratorCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEngineBase_BC8) {
			ItemStack sensorLocationCard = new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_ENGINE);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound getEngineData(TileEntity te) {
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
	}

}
