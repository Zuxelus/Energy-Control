package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.TimerBlock;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTimer extends TileEntityFacing implements ITickable, ITilePacketHandler {
	private int time;
	private int startingTime;
	private boolean invertRedstone;
	private boolean isTicks;
	private boolean isWorking;
	private boolean sendSignal;
	private boolean isPowered;

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
		if (isWorking)
			startingTime = time;
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
		return sendSignal;
	}

	public void onNeighborChange() { // server
		boolean newPowered = worldObj.getRedstonePower(pos.offset(rotation), rotation) > 0;
		if (newPowered != isPowered) {
			if (!isPowered && newPowered) {
				time = startingTime;
				setIsWorking(true);
			}
			isPowered = newPowered;
		}
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
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.setBoolean("isTicks", isTicks);
		tag.setBoolean("poweredBlock", sendSignal);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("timer"))
			time = tag.getInteger("timer");
		if (tag.hasKey("startingTime"))
			startingTime = tag.getInteger("startingTime");
		if (tag.hasKey("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.hasKey("isWorking"))
			isWorking = tag.getBoolean("isWorking");
		if (tag.hasKey("isTicks"))
			isTicks = tag.getBoolean("isTicks");
		if (tag.hasKey("poweredBlock"))
			sendSignal = tag.getBoolean("poweredBlock");
		if (tag.hasKey("isPowered"))
			isPowered = tag.getBoolean("isPowered");
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
		tag.setInteger("startingTime", startingTime);
		tag.setBoolean("invert", invertRedstone);
		tag.setBoolean("isWorking", isWorking);
		tag.setBoolean("isTicks", isTicks);
		tag.setBoolean("isPowered", isPowered);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void invalidate() {
		worldObj.notifyNeighborsOfStateChange(pos, worldObj.getBlockState(pos).getBlock());
		super.invalidate();
	}

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		if (!isWorking)
			return;
		if (time == 0) {
			setIsWorking(false);
			time = startingTime;
			return;
		}
		time--;
		if (time % 20 == 0)
			notifyBlockUpdate();
	}

	public void notifyBlockUpdate() {
		IBlockState iblockstate = worldObj.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (block instanceof TimerBlock) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
			if (sendSignal != newValue) {
				sendSignal = newValue;
				worldObj.notifyNeighborsOfStateChange(pos, block);
			}
			worldObj.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
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
