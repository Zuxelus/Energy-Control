package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.BlockMain;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityTimer extends TileEntityFacing implements ITilePacketHandler {
	private int time;
	private boolean invertRedstone;
	private boolean isTicks;
	private boolean isWorking;
	private boolean poweredBlock;

	public TileEntityTimer() {
		time = 0;
		invertRedstone = false;
		isTicks = false;
		isWorking = false;
	}

	public int getTime() {
		return time;
	}

	public String getTimeString() {
		if (isTicks)
			return Integer.toString(time);
		int seconds = time / 20; 
		return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
	}

	public void setTime(int value) {
		int old = time;
		time = value;
		if (!worldObj.isRemote && time != old)
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

	public boolean getIsWorking() {
		return isWorking;
	}

	public void setIsWorking(boolean value) {
		boolean old = isWorking;
		isWorking = value;
		if (!worldObj.isRemote && isWorking != old)
			notifyBlockUpdate();
	}

	public boolean getIsTicks() {
		return isTicks;
	}

	public void setIsTicks(boolean value) {
		boolean old = isTicks;
		isTicks = value;
		if (!worldObj.isRemote && isTicks != old)
			notifyBlockUpdate();
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
				setTime(tag.getInteger("value"));
			break;
		case 2:
			if (tag.hasKey("value"))
				setInvertRedstone(tag.getInteger("value") == 1);
			break;
		case 3:
			if (tag.hasKey("value"))
				setIsWorking(tag.getInteger("value") == 1);
			break;
		case 4:
			if (tag.hasKey("value"))
				setIsTicks(tag.getInteger("value") == 1);
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
				time = tag.getInteger("value");
			break;
		case 2:
			if (tag.hasKey("value"))
				isWorking = tag.getInteger("value") == 1;
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("isTicks", isTicks);
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
		if (tag.hasKey("timer"))
			time = tag.getInteger("timer");
		if (tag.hasKey("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.hasKey("isWorking"))
			isWorking = tag.getBoolean("isWorking");
		if (tag.hasKey("isTicks"))
			isTicks = tag.getBoolean("isTicks");
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
		tag.setInteger("timer", time);
		tag.setBoolean("invert", invertRedstone);
		tag.setBoolean("isWorking", isWorking);
		tag.setBoolean("isTicks", isTicks);
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
		if (!isWorking)
			return;
		if (time == 0) {
			setIsWorking(false);
			return;
		}
		time--;
		if (time % 20 == 0)
			notifyBlockUpdate();
	}

	public void notifyBlockUpdate() {
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (!(block instanceof BlockMain))
			return;
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (meta == BlockDamages.DAMAGE_TIMER) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
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

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}
}
