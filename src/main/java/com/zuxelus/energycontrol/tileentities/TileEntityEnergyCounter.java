package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.zlib.tileentities.TileEntitySinkSource;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.info.Info;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityEnergyCounter extends TileEntitySinkSource implements IWrenchable {
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
		onLoad();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		refreshData();
	}

	private void refreshData() {
		int upgradeCountTransormer = 0;
		ItemStack itemStack = getStackInSlot(0);
		if (itemStack != null && itemStack.isItemEqual(CrossModLoader.ic2.getItemStack("transformer")))
			upgradeCountTransormer = itemStack.stackSize;
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 4);
		if (worldObj != null && !worldObj.isRemote) {
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
	public boolean isItemValid(int slotIndex, ItemStack itemstack) { // ISlotItemFilter
		return itemstack.isItemEqual(CrossModLoader.ic2.getItemStack("transformer"));
	}

	// IWrenchable
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return facing.ordinal() != side;
	}

	@Override
	public short getFacing() {
		return (short) facing.ordinal();
	}

	@Override
	public void setFacing(short facing) {
		setFacing((int) facing);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return new ItemStack(ModItems.blockMain, 1, BlockDamages.DAMAGE_ENERGY_COUNTER);
	}
}
