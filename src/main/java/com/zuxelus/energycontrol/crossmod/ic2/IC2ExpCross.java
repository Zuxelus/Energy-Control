package com.zuxelus.energycontrol.crossmod.ic2;

import java.lang.reflect.Field;

import com.zuxelus.energycontrol.crossmod.EnergyStorageData;

import ic2.api.tile.IEnergyStorage;
import ic2.api.item.ICustomDamageItem;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityConversionGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
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
		if (stack.isEmpty())
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
		return !stack.isEmpty() && stack.getItem() instanceof ItemToolWrench;
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
			if (te instanceof TileEntityBaseGenerator) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				Field field = TileEntityBaseGenerator.class.getDeclaredField("energy");
				field.setAccessible(true);
				Energy energy = (Energy) field.get(te);
				tag.setDouble("storage", energy.getEnergy());
				tag.setDouble("maxStorage", energy.getCapacity());
				if (isActive(te)) {
					field = TileEntityBaseGenerator.class.getDeclaredField("production");
					field.setAccessible(true);
					tag.setDouble("production", (Double) field.get(te));
				}
				else
					tag.setDouble("production", 0);
				return tag;	
			}
			
			if (te instanceof TileEntityConversionGenerator) {
				NBTTagCompound tag = new NBTTagCompound();
				Field field = TileEntityConversionGenerator.class.getDeclaredField("production");
				field.setAccessible(true);
				tag.setDouble("production", (Double) field.get(te));
				if (te instanceof TileEntityStirlingGenerator) {
					tag.setInteger("type", 2);
					field = TileEntityStirlingGenerator.class.getDeclaredField("productionpeerheat");
					field.setAccessible(true);
					tag.setDouble("multiplier", (Double) field.get(te));
				}
				return tag;
			}
		} catch (Throwable t) { }
		return null;
	}
	
	private boolean isActive(TileEntity te) {
		if (te instanceof TileEntityGeoGenerator)
			return ((TileEntityBaseGenerator)te).getActive();
		if (te instanceof TileEntityBaseGenerator)
			return ((TileEntityBaseGenerator)te).isConverting();
		return false;
	}
}
