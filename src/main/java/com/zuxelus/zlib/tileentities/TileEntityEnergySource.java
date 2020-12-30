package com.zuxelus.zlib.tileentities;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityEnergySource extends TileEntityInventory implements IEnergySource, ISlotItemFilter, ITilePacketHandler {
	protected boolean addedToEnet;
	protected boolean allowEmit;
	protected int tier;
	protected int baseTier;
	protected int output;
	protected int baseOutput;
	protected double energy;
	protected double capacity;
	protected double baseCapacity;

	public TileEntityEnergySource(String name, int tier, int output, int capacity) {
		super(name);
		addedToEnet = false;
		allowEmit = true;
		energy = 0;
		this.tier = tier;
		this.baseTier = tier;
		this.output = output;
		this.baseOutput = output;
		this.capacity = capacity;
		this.baseCapacity = capacity;
	}

	public double getEnergy() {
		return energy;
	}

	public int getOutput() {
		return output;
	}

	public double getCapacity() {
		return capacity;
	}

	@Override
	public void setFacing(int meta) {
		onChunkUnload();
		super.setFacing(meta);
		onLoad();
	}

	public void setEnergy(double value) {
		energy = Math.min(value, capacity);
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		energy = tag.getDouble("energy");
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setDouble("energy", energy);
		return tag;
	}

	public void onLoad() {
		if (!addedToEnet && worldObj != null && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
		}
	}

	@Override
	public void invalidate() {
		onChunkUnload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		if (addedToEnet && worldObj != null && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	protected void handleDischarger(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null && energy < capacity && stack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) stack.getItem();
			if (item.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, capacity - energy, tier, false, false, false);
		}
	}

	protected void handleCharger(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null && energy > 0 && stack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) stack.getItem();
			double amount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, true, true);
			amount = Math.min(amount, energy);
			if (amount > 0)
				energy -= ElectricItem.manager.charge(stack, amount, tier, false, false);
		}
	}

	// IEnergySource
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection dir) {
		return dir == getFacingForge();
	}

	@Override
	public void drawEnergy(double amount) {
		energy -= amount;
	}

	@Override
	public double getOfferedEnergy() {
		return allowEmit ? energy >= output ? output : 0.0D : 0.0D; 
	}

	@Override
	public int getSourceTier() {
		return tier;
	}
}
