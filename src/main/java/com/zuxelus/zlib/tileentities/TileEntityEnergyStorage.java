package com.zuxelus.zlib.tileentities;

import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityEnergyStorage extends TileEntityInventory implements IEnergySink, IEnergyStorage, ISlotItemFilter, ITilePacketHandler {
	protected boolean addedToEnet;
	protected boolean allowEmit;
	protected int tier;
	protected int baseTier;
	protected int output;
	protected int baseOutput;
	protected double energy;
	protected double capacity;
	protected double baseCapacity;
	protected double energyReceived;

	public TileEntityEnergyStorage(String name, int tier, int output, int capacity) {
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
		energyReceived = 0;
	}

	public double getEnergy() {
		return energy;
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

	@Override
	public void updateEntity() {
		energyReceived = 0;
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
			double amount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, true, true);
			amount = Math.min(amount, energy);
			if (amount > 0)
				energy -= ElectricItem.manager.charge(stack, amount, tier, false, false);
		}
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection dir) {
		return dir != getFacingForge();
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
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if (energy >= capacity)
			return amount;
		energy += amount;
		energyReceived += amount;
		return 0.0D;
	}

	// IEnergyStorage
	@Override
	public int getStored() {
		return (int) energy;
	}

	@Override
	public void setStored(int energy) {
		this.energy = energy;
	}

	@Override
	public int addEnergy(int amount) {
		amount = (int) Math.min(capacity - energy, amount);
		energy += amount;
		return amount;
	}

	@Override
	public int getCapacity() {
		return (int) capacity;
	}

	@Override
	public int getOutput() {
		return output;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return output;
	}

	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return false;
	}
}
