package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import mekanism.common.capabilities.energy.EnergyCubeEnergyContainer;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class CrossModMekanism extends CrossMod {

	@Override
	public ItemStack getEnergyCard(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityEnergyCube) {
			ItemStack card = new ItemStack(ModItems.card_energy.get());
			ItemStackHelper.setCoordinates(card, pos);
			return card;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT getEnergyData(TileEntity te) {
		if (te instanceof TileEntityEnergyCube) {
			CompoundNBT tag = new CompoundNBT();
			EnergyCubeEnergyContainer container = ((TileEntityEnergyCube) te).getEnergyContainer();
			tag.putInt("type", 1);
			//tag.setString("euType", "");
			tag.putDouble("storage", container.getEnergy().doubleValue());
			tag.putDouble("maxStorage", container.getMaxEnergy().doubleValue());
			return tag;
		}
		return null;
	}

	@Override
	public List<IFluidTank> getAllTanks(TileEntity te) {
		if (te instanceof TileEntityFluidTank) {
			List<IFluidTank> result = new ArrayList<>();
			result.add(((TileEntityFluidTank) te).fluidTank);
			return result;
		}
		return null;
	}
}
