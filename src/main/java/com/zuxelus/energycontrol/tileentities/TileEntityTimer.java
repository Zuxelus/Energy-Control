package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.TimerBlock;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityFacing;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityTimer extends TileEntityFacing implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler {
	private int time;
	private int startingTime;
	private boolean invertRedstone;
	private boolean isTicks;
	private boolean isWorking;
	private boolean sendSignal;
	private boolean isPowered;

	public TileEntityTimer(TileEntityType<?> type) {
		super(type);
		time = 0;
		invertRedstone = false;
		isTicks = false;
		isWorking = false;
	}

	public TileEntityTimer() {
		this(ModTileEntityTypes.timer.get());
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
		if (!world.isRemote && time != old)
			notifyBlockUpdate();
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!world.isRemote && invertRedstone != old)
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
		if (!world.isRemote && isWorking != old)
			notifyBlockUpdate();
	}

	public boolean getIsTicks() {
		return isTicks;
	}

	public void setIsTicks(boolean value) {
		boolean old = isTicks;
		isTicks = value;
		if (!world.isRemote && isTicks != old)
			notifyBlockUpdate();
	}

	public boolean getPowered() {
		return sendSignal;
	}

	public void onNeighborChange(Block fromBlock, BlockPos fromPos) { // server
		boolean newPowered = world.getRedstonePower(pos.offset(rotation), rotation) > 0;
		if (newPowered != isPowered) {
			if (!isPowered && newPowered) {
				time = startingTime;
				setIsWorking(true);
			}
			isPowered = newPowered;
		}
	}

	@Override
	public void onServerMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				setTime(tag.getInt("value"));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getInt("value") == 1);
			break;
		case 3:
			if (tag.contains("value"))
				setIsWorking(tag.getInt("value") == 1);
			break;
		case 4:
			if (tag.contains("value"))
				setIsTicks(tag.getInt("value") == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundNBT tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				time = tag.getInt("value");
			break;
		case 2:
			if (tag.contains("value"))
				isWorking = tag.getInt("value") == 1;
			break;
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("poweredBlock", sendSignal);
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("timer"))
			time = tag.getInt("timer");
		if (tag.contains("startingTime"))
			startingTime = tag.getInt("startingTime");
		if (tag.contains("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.contains("isWorking"))
			isWorking = tag.getBoolean("isWorking");
		if (tag.contains("isTicks"))
			isTicks = tag.getBoolean("isTicks");
		if (tag.contains("poweredBlock"))
			sendSignal = tag.getBoolean("poweredBlock");
		if (tag.contains("isPowered"))
			isPowered = tag.getBoolean("isPowered");
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("timer", time);
		tag.putInt("startingTime", startingTime);
		tag.putBoolean("invert", invertRedstone);
		tag.putBoolean("isWorking", isWorking);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("isPowered", isPowered);
		return tag;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		return writeProperties(super.write(tag));
	}

	@Override
	public void remove() {
		world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
		super.remove();
	}

	@Override
	public void tick() {
		if (world.isRemote)
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
		BlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (block instanceof TimerBlock) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
			if (sendSignal != newValue) {
				sendSignal = newValue;
				world.notifyNeighborsOfStateChange(pos, block);
			}
			world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTimer(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.timer.get().getTranslationKey());
	}
}
