package com.zuxelus.energycontrol.crossmod.ic2;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.tile.IEnergyStorage;
import ic2.api.item.ICustomDamageItem;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.item.reactor.ItemReactorLithiumCell;
import ic2.core.item.reactor.ItemReactorMOX;
import ic2.core.item.reactor.ItemReactorUranium;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class IC2ExpCross extends IC2Cross {

	@Override
	public int getNuclearCellTimeLeft(ItemStack stack) {
		if (stack == null)
			return 0;
		Item item = stack.getItem();
		if (item instanceof ItemReactorUranium || item instanceof ItemReactorLithiumCell
				|| item instanceof ItemReactorMOX)
			return ((ICustomDamageItem)item).getMaxCustomDamage(stack) - ((ICustomDamageItem)item).getCustomDamage(stack);

		return 0;
	}

	@Override
	public IC2Type getType() {
		return IC2Type.EXP;
	}

	@Override
	public boolean isWrench(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemToolWrench;
	}

	@Override
	public boolean isSteamReactor(TileEntity par1) {
		return false;
	}

	@Override
	public EnergyStorageData getEnergyStorageData(TileEntity target) {
		if (target instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) target;
			EnergyStorageData result = new EnergyStorageData();
			result.values.add((double) storage.getCapacity());
			result.values.add((double) storage.getStored());
			result.type = EnergyStorageData.TARGET_TYPE_IC2;
			return result;
		}
		return null;
	}

	@Override
	public ReactorInfo getReactorInfo(TileEntity par1) {
		if (par1 == null || !(par1 instanceof TileEntityNuclearReactorElectric))
			return null;
		TileEntityNuclearReactorElectric reactor = (TileEntityNuclearReactorElectric) par1;
		ReactorInfo info = new ReactorInfo();
		info.isOnline = reactor.getActive();
		info.outTank = reactor.getoutputtank().getFluidAmount();
		info.inTank = reactor.getinputtank().getFluidAmount();
		info.emitHeat = reactor.EmitHeat;
		info.coreTemp = (int) (((double) reactor.getHeat() / (double) reactor.getMaxHeat()) * 100D);
		return info;
	}

	@Override
	public boolean isMultiReactorPart(TileEntity par1) {
		if (par1 instanceof TileEntityReactorRedstonePort || par1 instanceof TileEntityReactorAccessHatch)
			return true;
		return false;
	}

	@Override
	public NBTTagCompound getGeneratorData(TileEntity te) {
		try {
			NBTTagCompound tag = new NBTTagCompound();
			/*Field field = null;
			Field fieldProd = null;
			if (te instanceof TileEntityBaseGenerator) {
				field = TileEntityBaseGenerator.class.getDeclaredField("energy");
				fieldProd = TileEntityBaseGenerator.class.getDeclaredField("production");
				fieldProd.setAccessible(true);
				if (!((TileEntityBaseGenerator) te).isConverting())
					data.values.add((double) 0);
				else
					data.values.add((Double) fieldProd.get(te));
			} else if (te instanceof TileEntityGeoGenerator) {
				field = TileEntityGeoGenerator.class.getDeclaredField("energy");
				fieldProd = TileEntityGeoGenerator.class.getDeclaredField("production");
				fieldProd.setAccessible(true);
				if (!((TileEntityGeoGenerator) te).isConverting())
					data.values.add((double) 0);
				else
					data.values.add((double) (Integer) fieldProd.get(te));
			}
			if (field == null)
				return null;
			field.setAccessible(true);
			Energy energy = (Energy) field.get(te);
			data.values.add(energy.getEnergy());
			data.values.add(energy.getCapacity());
			return data;*/
		} catch (Throwable t) { }
		return null;
	}
}
