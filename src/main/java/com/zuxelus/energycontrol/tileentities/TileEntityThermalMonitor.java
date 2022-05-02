package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.RemoteThermalMonitor;
import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityThermalMonitor extends TileEntityInventory implements ITilePacketHandler {
	private int heatLevel;
	private boolean invertRedstone;
	protected int status;
	private boolean poweredBlock;

	protected int updateTicker;
	protected int tickRate;

	public TileEntityThermalMonitor() {
		super("tile.thermal_monitor.name");
		invertRedstone = false;
		heatLevel = 500;
		updateTicker = 0;
		tickRate = EnergyControl.config.thermalMonitorRefreshPeriod;
		status = -1;
	}

	public int getHeatLevel() {
		return heatLevel;
	}

	public void setHeatLevel(int value) {
		int old = heatLevel;
		heatLevel = value;
		if (!worldObj.isRemote && heatLevel != old)
			notifyBlockUpdate();
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!worldObj.isRemote && invertRedstone != old)
			notifyBlockUpdate();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int newStatus) {
		status = newStatus;
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setHeatLevel(tag.getInteger("value"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setInvertRedstone(tag.getInteger("value") == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) { }

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setInteger("status", status);
		tag.setBoolean("poweredBlock", poweredBlock);
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
		if (tag.hasKey("heatLevel"))
			heatLevel = tag.getInteger("heatLevel");
		if (tag.hasKey("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.hasKey("status"))
			setStatus(tag.getInteger("status"));
		if (tag.hasKey("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("heatLevel", heatLevel);
		tag.setBoolean("invert", invertRedstone);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Override
	public void invalidate() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		super.invalidate();
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
	
		if (updateTicker-- > 0)
			return;
		updateTicker = tickRate;
		checkStatus();
	}

	protected void checkStatus() {
		int heat = CrossModLoader.getHeat(worldObj, xCoord, yCoord, zCoord);
		int newStatus = heat == -1 ? -2 : heat >= heatLevel ? 1 : 0;

		if (newStatus != status) {
			status = newStatus;
			notifyBlockUpdate();
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		}
	}

	public void notifyBlockUpdate() {
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (block instanceof ThermalMonitor || block instanceof RemoteThermalMonitor) {
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, block);
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// ------- Inventory ------- 
	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockThermalMonitor);
	}
}
