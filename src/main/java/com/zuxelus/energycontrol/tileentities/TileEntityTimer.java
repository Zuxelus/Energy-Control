package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.TimerBlock;
import com.zuxelus.energycontrol.containers.ContainerTimer;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.BlockEntityFacing;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileEntityTimer extends BlockEntityFacing implements ExtendedMenuProvider<BlockPos>, ITilePacketHandler {
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
		this(ModTileEntityTypes.timer, pos, state);
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
		if (!level.isClientSide() && time != old)
			notifyBlockUpdate();
	}

	public boolean getInvertRedstone() {
		return invertRedstone;
	}

	public void setInvertRedstone(boolean value) {
		boolean old = invertRedstone;
		invertRedstone = value;
		if (!level.isClientSide() && invertRedstone != old)
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
		if (!level.isClientSide() && isWorking != old)
			notifyBlockUpdate();
	}

	public boolean getIsTicks() {
		return isTicks;
	}

	public void setIsTicks(boolean value) {
		boolean old = isTicks;
		isTicks = value;
		if (!level.isClientSide() && isTicks != old)
			notifyBlockUpdate();
	}

	public boolean getPowered() {
		return sendSignal;
	}

	public void onNeighborChange(Block fromBlock) { // server
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
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("value"))
				setTime(tag.getIntOr("value", 0));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getIntOr("value", 0) == 1);
			break;
		case 3:
			if (tag.contains("value"))
				setIsWorking(tag.getIntOr("value", 0) == 1);
			break;
		case 4:
			if (tag.contains("value"))
				setIsTicks(tag.getIntOr("value", 0) == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("value"))
				time = tag.getIntOr("value", 0);
			break;
		case 2:
			if (tag.contains("value"))
				isWorking = tag.getIntOr("value", 0) == 1;
			break;
		}
	}

	@Override
	protected void writeUpdateData(ValueOutput tag) {
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("poweredBlock", sendSignal);
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		time = tag.getIntOr("timer", time);
		startingTime = tag.getIntOr("startingTime", startingTime);
		invertRedstone = tag.getBooleanOr("invert", invertRedstone);
		isWorking = tag.getBooleanOr("isWorking", isWorking);
		isTicks = tag.getBooleanOr("isTicks", isTicks);
		sendSignal = tag.getBooleanOr("poweredBlock", sendSignal);
		isPowered = tag.getBooleanOr("isPowered", isPowered);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putInt("timer", time);
		tag.putInt("startingTime", startingTime);
		tag.putBoolean("invert", invertRedstone);
		tag.putBoolean("isWorking", isWorking);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("isPowered", isPowered);
	}

	@Override
	public void setRemoved() {
		// also called while the chunk unloads; touching the world then loads the chunk again and never finishes unloading
		if (Screen.isLoaded(level, worldPosition))
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
		if (level.isClientSide())
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

	@Override
	protected void notifyBlockUpdate() {
		BlockState state = level.getBlockState(worldPosition);
		Block block = state.getBlock();
		if (block instanceof TimerBlock) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
			if (sendSignal != newValue) {
				sendSignal = newValue;
				level.updateNeighborsAt(worldPosition, block);
			}
			level.sendBlockUpdated(worldPosition, state, state, 2);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	// NamedScreenHandlerFactory
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTimer(windowId, inventory, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable(ModItems.timer.getDescriptionId());
	}

	@Override
	public BlockPos getScreenOpeningData(ServerPlayer player) {
		return worldPosition;
	}
}
