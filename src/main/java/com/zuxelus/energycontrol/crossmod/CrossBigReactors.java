package com.zuxelus.energycontrol.crossmod;

import java.util.List;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardBigReactors;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitBigReactors;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;
import com.zuxelus.energycontrol.recipes.Recipes;
import com.zuxelus.energycontrol.utils.DataHelper;
import com.zuxelus.energycontrol.utils.FluidInfo;

import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorPartBase;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbinePartBase;
import erogenousbeef.core.common.CoordTriplet;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;

public class CrossBigReactors extends CrossModBase {

	@Override
	public NBTTagCompound getEnergyData(TileEntity te) {
		// TODO Auto-generated method stub
		return super.getEnergyData(te);
	}

	@Override
	public List<FluidInfo> getAllTanks(TileEntity te) {
		FluidTankInfo[] info = null;
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;
			if (reactor.isPassivelyCooled())
				return null;
			return FluidInfo.toFluidInfoList(reactor.getCoolantContainer().getTankInfo(-1));
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;
			return FluidInfo.toFluidInfoList(turbine.getTankInfo());
		}
		return null;
	}

	@Override
	public NBTTagCompound getCardData(TileEntity te) {
		NBTTagCompound tag = new NBTTagCompound();
		if (te instanceof TileEntityReactorPartBase) {
			MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
			if (reactor == null)
				return null;

			tag.setBoolean(DataHelper.ACTIVE, reactor.getActive());
			tag.setBoolean("cooling", reactor.isPassivelyCooled());
			tag.setDouble("fuelHeat", reactor.getFuelHeat());
			tag.setDouble(DataHelper.HEAT, reactor.getReactorHeat());
			tag.setDouble(DataHelper.ENERGY, reactor.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, 1.0E7D);
			if (reactor.isPassivelyCooled())
				tag.setDouble(DataHelper.OUTPUT, reactor.getEnergyGeneratedLastTick());
			else
				tag.setDouble(DataHelper.OUTPUTMB, reactor.getEnergyGeneratedLastTick());
			tag.setInteger("rods", reactor.getFuelRodCount());
			tag.setInteger("fuel", reactor.getFuelAmount());
			tag.setInteger("waste", reactor.getWasteAmount());
			tag.setInteger("fuelCapacity", reactor.getCapacity());
			tag.setDouble("consumption", reactor.getFuelConsumedLastTick());
			CoordTriplet min = reactor.getMinimumCoord();
			CoordTriplet max = reactor.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.x - min.x + 1, max.y - min.y + 1, max.z - min.z + 1));
			return tag;
		}
		if (te instanceof TileEntityTurbinePartBase) {
			MultiblockTurbine turbine = ((TileEntityTurbinePartBase) te).getTurbine();
			if (turbine == null)
				return null;

			tag.setBoolean(DataHelper.ACTIVE, turbine.getActive());
			tag.setDouble(DataHelper.ENERGY, turbine.getEnergyStored());
			tag.setDouble(DataHelper.CAPACITY, turbine.getMaxEnergyStored(null));
			tag.setDouble(DataHelper.OUTPUT, turbine.getEnergyGeneratedLastTick());
			tag.setDouble("speed", turbine.getRotorSpeed());
			tag.setDouble("speedMax", turbine.getMaxRotorSpeed());
			tag.setDouble("efficiency", turbine.getRotorEfficiencyLastTick());
			tag.setDouble("consumption", turbine.getFluidConsumedLastTick());
			tag.setInteger("blades", turbine.getNumRotorBlades());
			tag.setInteger("mass", turbine.getRotorMass());
			CoordTriplet min = turbine.getMinimumCoord();
			CoordTriplet max = turbine.getMaximumCoord();
			tag.setString("size", String.format("%sx%sx%s",max.x - min.x + 1, max.y - min.y + 1, max.z - min.z + 1));
			return tag;
		}
		return null;
	}

	@Override
	public int getHeat(World world, int x, int y, int z) {
		TileEntity te;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			te = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
			if (te instanceof TileEntityReactorPartBase) {
				MultiblockReactor reactor = ((TileEntityReactorPartBase) te).getReactorController();
				if (reactor != null)
					return (int) reactor.getReactorHeat();
			}
		}
		return -1;
	}

	@Override
	public void registerItems() {
		ItemKitMain.register(ItemKitBigReactors::new);
		ItemCardMain.register(ItemCardBigReactors::new);
	}

	@Override
	public void loadRecipes() {
		Recipes.addShapedRecipe(ModItems.itemKit, ItemCardType.KIT_BIG_REACTORS,
			new Object[] { "RF", "PB", 'P', Items.paper, 'R', "dustRedstone",
				'F', new ItemStack(ModItems.itemComponent, 1, ItemComponent.RADIO_TRANSMITTER), 'B', "ingotYellorium" });

		Recipes.addKitRecipe(ItemCardType.KIT_BIG_REACTORS, ItemCardType.CARD_BIG_REACTORS);
	}
}
