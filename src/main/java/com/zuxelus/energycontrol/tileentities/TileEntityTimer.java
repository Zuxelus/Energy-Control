package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.TimerBlock;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityTimer extends BlockEntityFacing implements MenuProvider, ITilePacketHandler {
	private int time;
	private int startingTime;
	private boolean invertRedstone;
	private boolean isTicks;
	private boolean isWorking;
	private boolean sendSignal;
	private boolean isPowered;

	public TileEntityTimer(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		time = 0;
		invertRedstone = false;
		isTicks = false;
		isWorking = false;
	}

	public TileEntityTimer(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.timer.get(), pos, state);
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
		if (!level.isClientSide && time != old)
			notifyBlockUpdate();
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!level.isClientSide && invertRedstone != old)
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
		if (!level.isClientSide && isWorking != old)
			notifyBlockUpdate();
	}

	public boolean getIsTicks() {
		return isTicks;
	}

	public void setIsTicks(boolean value) {
		boolean old = isTicks;
		isTicks = value;
		if (!level.isClientSide && isTicks != old)
			notifyBlockUpdate();
	}

	public boolean getPowered() {
		return sendSignal;
	}

	public void onNeighborChange(Block fromBlock, BlockPos fromPos) { // server
		boolean newPowered = level.getSignal(worldPosition.relative(rotation), rotation) > 0;
		if (newPowered != isPowered) {
			if (!isPowered && newPowered) {
				time = startingTime;
				setIsWorking(true);
			}
			isPowered = newPowered;
		}
	}

	@Override
	public void onServerMessageReceived(CompoundTag tag) {
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
	public void onClientMessageReceived(CompoundTag tag) {
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
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("poweredBlock", sendSignal);
		return tag;
	}

	@Override
	protected void readProperties(CompoundTag tag) {
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
	public void load(CompoundTag tag) {
		super.load(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
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
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		writeProperties(tag);
	}

	@Override
	public void setRemoved() {
		level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		super.setRemoved();
	}

	public static void tickStatic(Level level, BlockPos pos, BlockState state, BlockEntity be) {
		if (!(be instanceof TileEntityTimer))
			return;
		TileEntityTimer te = (TileEntityTimer) be;
		te.tick();
	}

	protected void tick() {
		if (level.isClientSide)
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
		BlockState iblockstate = level.getBlockState(worldPosition);
		Block block = iblockstate.getBlock();
		if (block instanceof TimerBlock) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
			if (sendSignal != newValue) {
				sendSignal = newValue;
				level.updateNeighborsAt(worldPosition, block);
			}
			level.sendBlockUpdated(worldPosition, iblockstate, iblockstate, 2);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// MenuProvider
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTimer(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.timer.get().getDescriptionId());
	}
}
