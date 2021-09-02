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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityTimer extends TileEntityFacing implements ITickableTileEntity, INamedContainerProvider, ITilePacketHandler {
	private int time;
	private boolean invertRedstone;
	private boolean isTicks;
	private boolean isWorking;
	private boolean poweredBlock;

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
		return poweredBlock;
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
		CompoundNBT tag = new CompoundNBT();
		tag = writeProperties(tag);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("poweredBlock", poweredBlock);
		return new SUpdateTileEntityPacket(getBlockPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readProperties(pkt.getTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag = writeProperties(tag);
		tag.putBoolean("isTicks", isTicks);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("timer"))
			time = tag.getInt("timer");
		if (tag.contains("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.contains("isWorking"))
			isWorking = tag.getBoolean("isWorking");
		if (tag.contains("isTicks"))
			isTicks = tag.getBoolean("isTicks");
		if (tag.contains("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("timer", time);
		tag.putBoolean("invert", invertRedstone);
		tag.putBoolean("isWorking", isWorking);
		tag.putBoolean("isTicks", isTicks);
		return tag;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		return writeProperties(super.save(tag));
	}

	@Override
	public void setRemoved() {
		level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		super.setRemoved();
	}

	@Override
	public void tick() {
		if (level.isClientSide)
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
		BlockState iblockstate = level.getBlockState(worldPosition);
		Block block = iblockstate.getBlock();
		if (block instanceof TimerBlock) {
			boolean newValue = time > 0 && isWorking ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				level.updateNeighborsAt(worldPosition, block);
			}
			level.sendBlockUpdated(worldPosition, iblockstate, iblockstate, 2);
		}
	}

	@Override
	protected boolean hasRotation() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getViewDistance() {
		return 65536.0D;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTimer(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.timer.get().getDescriptionId());
	}
}
