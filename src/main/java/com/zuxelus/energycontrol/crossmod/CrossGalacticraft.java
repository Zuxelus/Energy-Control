package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.utils.FluidInfo;

import ic2.api.item.IC2Items;
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

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
		if (te instanceof IFluidHandler) {
			FluidTankInfo[] info = ((IFluidHandler) te).getTankInfo(null);
			List<FluidInfo> result = new ArrayList<>();
			for (FluidTankInfo tank : info)
				result.add(new FluidInfo(tank.fluid, tank.capacity));
			if (result.size() > 0)
				return result;
			for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
				info = ((IFluidHandler) te).getTankInfo(facing);
				for (FluidTankInfo tank : info)
					result.add(new FluidInfo(tank.fluid, tank.capacity));
			}
			return result;
		}
		return null;
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GALACTICRAFT,
			new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone",
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', new ItemStack(GCBlocks.aluminumWire) });

		Recipes.addKitRecipe(ItemCardType.KIT_GALACTICRAFT, ItemCardType.CARD_GALACTICRAFT);
	}
}
