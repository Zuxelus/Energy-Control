package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.utils.FluidInfo;

import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.GCBlocks;
import micdoodle8.mods.galacticraft.core.wrappers.IFluidHandlerWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossGalacticraft extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof TileEntityAFSU)
			return null;
		if (te instanceof IEnergyHandlerGC) {
			NBTTagCompound tag = new NBTTagCompound();
			IEnergyHandlerGC storage = (IEnergyHandlerGC) te;
			tag.setInteger("type", 11);
			tag.setDouble("storage", storage.getEnergyStoredGC(null));
			tag.setDouble("maxStorage", storage.getMaxEnergyStoredGC(null));
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		if (te instanceof IFluidHandlerWrapper) {
			FluidTankInfo[] info = ((IFluidHandlerWrapper) te).getTankInfo(null);
			List<FluidInfo> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidInfo(tank.fluid, tank.capacity));
			if (result.size() > 0)
				return result;
			for (EnumFacing facing : EnumFacing.VALUES) {
				info = ((IFluidHandlerWrapper) te).getTankInfo(facing);
				for (FluidTankInfo tank : info)
					result.add(new FluidInfo(tank.fluid, tank.capacity));
			}
			return result;
		}
		return null;
	}

	@Override
	public void loadRecipes() { // 1.10.2 and less
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GALACTICRAFT,
			new Object[] { "RF", "PB", 'P', Items.PAPER, 'R', "dustRedstone", 'B', GCBlocks.aluminumWire,
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER) });
	}
}
