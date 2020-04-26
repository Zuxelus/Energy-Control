package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ISlotItemFilter;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.Info;
import ic2.api.item.IC2Items;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityEnergyCounter extends TileEntityInventory
		implements ITickable, ISlotItemFilter, IEnergySink, IEnergySource, ITilePacketHandler {
	private static final int BASE_PACKET_SIZE = 32;
	private boolean init;

	protected int updateTicker;
	protected int tickRate;

	public double counter;
	private boolean addedToEnet;
	
	  public int tier;
	  public int output;
	  public double energy;	

	public TileEntityEnergyCounter() {
		super("block.EnergyCounter");
		addedToEnet = false;
		counter = 0.0;
		
		energy = 0;
		tier = 1;
		output = BASE_PACKET_SIZE;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				counter = tag.getInteger("value");
			break;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		return new SPacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		counter = tag.getDouble("counter");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setDouble("counter", counter);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void onLoad() {
		if (!addedToEnet && !world.isRemote && Info.isIc2Available()) {
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
		if (addedToEnet && !world.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void update() {
		if (!addedToEnet)
			onLoad();
		if (!init) {
			init = true;
			markDirty();
		}
		if (world.isRemote)
			return;
		TileEntity neighbor = world.getTileEntity(pos.offset(facing));
		if (neighbor instanceof TileEntityCable) {
			NodeStats node = EnergyNet.instance.getNodeStats(this);
			if (node != null)
				counter += node.getEnergyOut();
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		int upgradeCountTransormer = 0;
		ItemStack itemStack = getStackInSlot(0);
		if (!itemStack.isEmpty() && itemStack.isItemEqual(IC2Items.getItem("upgrade","transformer")))
			upgradeCountTransormer = itemStack.getCount();
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 3);
		if (world != null && !world.isRemote) {
			output = BASE_PACKET_SIZE * (int) Math.pow(4D, upgradeCountTransormer);
			tier = upgradeCountTransormer + 1;

			if (addedToEnet) {
				EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
				MinecraftForge.EVENT_BUS.post(event);
			}
			addedToEnet = false;
			EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
			MinecraftForge.EVENT_BUS.post(event);
			addedToEnet = true;
		}
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}
	
	@Override
	public boolean isItemValid(int slotIndex, ItemStack itemstack) { // ISlotItemFilter
		return itemstack.isItemEqual(IC2Items.getItem("upgrade","transformer"));
	}	

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing dir) {
		return dir != getFacing();

	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing dir) {
		return dir == getFacing();
	}

	@Override
	public void drawEnergy(double amount) {
		this.energy -= amount;
		
	}

	@Override
	public double getOfferedEnergy() {
		if (this.energy >= this.output)
			return Math.min(this.energy, this.output);
		return 0.0D;
	}

	@Override
	public int getSourceTier() {
		return this.tier;
	}

	@Override
	public double getDemandedEnergy() {
		return output - energy;
	}

	@Override
	public int getSinkTier() {
		return tier;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if (energy >= output)
			return amount;
		this.energy += amount;
		return 0.0D;
	}
}
