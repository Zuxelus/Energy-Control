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
		counter += amount;
	}

	@Override
	public void updateEntity() {
		onLoad(); // 1.7.10
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
		return new ItemStack(ModItems.blockEnergyCounter);
	}
}
