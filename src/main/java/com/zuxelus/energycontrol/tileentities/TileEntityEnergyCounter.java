package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.info.Info;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityEnergyCounter extends TileEntityEnergyStorage {
	private static final int BASE_PACKET_SIZE = 32;
	private boolean init;
	protected int updateTicker;
	protected int tickRate;
	public double counter;

	public TileEntityEnergyCounter() {
		super("tile.energy_counter.name", 1, BASE_PACKET_SIZE, BASE_PACKET_SIZE * 2);
		counter = 0.0;
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
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				counter = tag.getDouble("value");
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
		super.onLoad();
		if (!init) {
			init = true;
			refreshData();
		}
	}

	@Override
	public void drawEnergy(double amount) {
		super.drawEnergy(amount);
		counter += amount;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		refreshData();
	}

	private void refreshData() {
		int upgradeCountTransormer = 0;
		ItemStack itemStack = getStackInSlot(0);
		if (!itemStack.isEmpty() && itemStack.isItemEqual(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("transformer")))
			upgradeCountTransormer = itemStack.getCount();
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 4);
		if (world != null && !world.isRemote) {
			output = BASE_PACKET_SIZE * (int) Math.pow(4D, upgradeCountTransormer);
			capacity = output * 2;
			tier = upgradeCountTransormer + 1;

			if (Info.isIc2Available() ) {
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
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		return stack.isItemEqual(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("transformer"));
	}

	// IEnergySource
	@Override
	public double getOfferedEnergy() {
		return allowEmit ? energy >= output ? output : energy : 0.0D; 
	}
}
