package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

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
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;

public abstract class TileEntityEnergyStorage extends TileEntityInventory implements IEnergySink, IEnergySource, ISlotItemFilter, ITilePacketHandler {
	protected boolean addedToEnet;
	protected boolean allowEmit;
	protected int tier;
	protected int output;
	protected double energy;
	protected double capacity;

	public TileEntityEnergyStorage(String name, int tier, int output, int capacity) {
		super(name);
		addedToEnet = false;
		allowEmit = true;
		energy = 0;
		this.tier = tier;
		this.output = output;
		this.capacity = capacity;
	}

	public double getEnergy() {
		return energy;
	}

	public int getOutput() {
		return output;
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

	@Override
	public void onLoad() {
		if (!addedToEnet && world != null && !world.isRemote && Info.isIc2Available()) {
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
		if (addedToEnet && world != null && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	protected void handleDischarger(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty() && energy < capacity && stack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) stack.getItem();
			if (ielectricitem.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, capacity - energy, tier, false, false, false);
		}
	}

	protected void handleCharger(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty() && energy > 0 && stack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) stack.getItem();
			int tier = item.getTier(stack);
			double amount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, true, true);
			amount = Math.min(amount, energy);
			if (amount > 0)
				energy -= ElectricItem.manager.charge(stack, amount, tier, false, false);
		}
	}

	// IEnergySource
	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
		return side == getFacing();
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

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
		return side != getFacing();
	}

	@Override
	public double getDemandedEnergy() {
		return Math.min(capacity - energy, output);
	}

	@Override
	public int getSinkTier() {
		return tier;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (energy >= capacity)
			return amount;
		energy += amount;
		return 0.0D;
	}
}
