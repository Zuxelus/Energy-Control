package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.IC2ReactorHelper;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityAverageCounter extends TileEntityEnergyStorage {
	private static final int BASE_PACKET_SIZE = 32;
	protected static final int DATA_POINTS = 11 * 20;
	private boolean init;

	protected double[] data;
	protected int index;
	protected int updateTicker;
	protected int tickRate;

	protected short prevPeriod;
	public short period;
	protected double clientAverage = -1;

	public TileEntityAverageCounter() {
		super("tile.average_counter.name", 1, BASE_PACKET_SIZE, BASE_PACKET_SIZE * 2);
		data = new double[DATA_POINTS];
		index = 0;
		tickRate = 20;
		updateTicker = tickRate;
		prevPeriod = period = 1;
	}

	public double getClientAverage() {
		if (clientAverage == -1)
			return getAverage();
		return clientAverage;
	}

	private double getAverage() {
		int start = DATA_POINTS + index - period * 20;
		double sum = 0;
		for (int i = 0; i < period * 20; i++)
			sum += data[(start + i) % DATA_POINTS];
		clientAverage = sum / period / 20;
		return clientAverage;
	}

	private void setPeriod(short p) {
		period = p;
		if (!worldObj.isRemote && prevPeriod != period)
			notifyBlockUpdate();
		prevPeriod = period;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value")) {
				int event = tag.getInteger("value");
				if (event == 0) {
					for (int i = 0; i < DATA_POINTS; i++)
						data[i] = 0;

					updateTicker = tickRate;
					index = 0;
				} else
					setPeriod((short) event);
			}
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
				clientAverage = tag.getDouble("value");
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if (!worldObj.isRemote)
			return;
		readProperties(pkt.func_148857_g());
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		index = tag.getInteger("dataIndex");
		updateTicker = tag.getInteger("updateTicker");
		prevPeriod = period = tag.getShort("period");

		for (int i = 0; i < DATA_POINTS; i++)
			data[i] = tag.getDouble("point-" + i);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("dataIndex", index);
		tag.setInteger("updateTicker", updateTicker);
		tag.setShort("period", period);

		for (int i = 0; i < DATA_POINTS; i++)
			tag.setDouble("point-" + i, data[i]);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
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
		data[index] += amount;
	}

	@Override
	public void updateEntity() {
		onLoad(); // 1.7.10
		if (worldObj.isRemote)
			return;

		index = (index + 1) % DATA_POINTS;
		data[index] = 0;
		getAverage();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		refreshData();
	}

	private void refreshData() {
		int upgradeCountTransormer = 0;
		ItemStack stack = getStackInSlot(0);
		if (stack != null && stack.isItemEqual(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("transformer")))
			upgradeCountTransormer = stack.stackSize;
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 4);
		if (worldObj != null && !worldObj.isRemote) {
			output = BASE_PACKET_SIZE * (int) Math.pow(4D, upgradeCountTransormer);
			capacity = output * 2;
			tier = upgradeCountTransormer + 1;

			if (Loader.isModLoaded(ModIDs.IC2)) {
				if (addedToEnet) {
					IC2ReactorHelper.energyLoadEvent(this, false);
				}
				addedToEnet = false;
				IC2ReactorHelper.energyLoadEvent(this, true);
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

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockAverageCounter);
	}
}
