package com.zuxelus.energycontrol.crossmod;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.hooks.GregTechHooks;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardGregTech;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitGregTech;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.tileentities.boilers.MTEBoiler;
import gregtech.common.tileentities.boilers.MTEBoilerSolar;
import gregtech.tileentity.tanks.MultiTileEntityBarrelMetal;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class CrossGregTechGTNH extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		if (te instanceof BaseMetaTileEntity) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(DataHelper.EUTYPE, "EU");
			tag.setDouble(DataHelper.ENERGY, ((BaseMetaTileEntity) te).getStoredEU());
			tag.setDouble(DataHelper.CAPACITY, ((BaseMetaTileEntity) te).getEUCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		List<FluidInfo> result = new ArrayList<>();
		if (te instanceof BaseMetaTileEntity) {
			FluidTankInfo[] list = ((BaseMetaTileEntity) te).getTankInfo(ForgeDirection.UNKNOWN);
			for (FluidTankInfo tank: list)
				result.add(new FluidInfo(tank.fluid, tank.capacity));
			return result;
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		if (te instanceof BaseMetaTileEntity) {
			NBTTagCompound tag = new NBTTagCompound();
			BaseMetaTileEntity meta = (BaseMetaTileEntity) te;
			IMetaTileEntity tile = meta.getMetaTileEntity();
			if (tile != null) {
				if (tile instanceof MTEBoiler) {
					if (tile instanceof MTEBoilerSolar) {
						tag.setDouble(DataHelper.OUTPUTL, ((MTEBoilerSolar) tile).getProductionPerSecond());
					}
					FluidTankInfo[] info = meta.getTankInfo(ForgeDirection.UNKNOWN);
					if (info != null) {
						if (info.length >= 1) {
							FluidTankInfo tank = info[1];
							if (tank != null && tank.fluid != null) {
								FluidStack stack = tank.fluid;
								FluidInfo.addTank(DataHelper.TANK, tag, stack, "L");
								tag.setDouble(DataHelper.CAPACITYMB, tank.capacity);
							}
						}
					}
				}
				return tag;
			}
			tag.setBoolean(DataHelper.ACTIVE, meta.isActive());
			FluidTankInfo[] info = meta.getTankInfo(ForgeDirection.UNKNOWN);
			if (info != null) {
				if (info.length >= 1) {
					FluidTankInfo tank = info[1];
					if (tank != null && tank.fluid != null) {
						FluidStack stack = tank.fluid;
						FluidInfo.addTank(DataHelper.TANK, tag, stack, "L");
						tag.setDouble(DataHelper.CAPACITYMB, tank.capacity);
					}
				}
			}
			tag.setDouble(DataHelper.OUTPUT, meta.getOutputEnergyUnitsPerTick());
			tag.setDouble(DataHelper.ENERGY, meta.getStoredEU());
			tag.setDouble(DataHelper.CAPACITY, meta.getEUCapacity());
			return tag;
		}
		return null;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitGregTech::new);
		ItemCardMain.register(ItemCardGregTech::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_GREGTECH,
				new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dyeGray",
					'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotBronze" });

		Recipes.addKitRecipe(ItemCardType.KIT_GREGTECH, ItemCardType.CARD_GREGTECH);
	}
}
