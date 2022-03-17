package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.RemoteThermalMonitor;
import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityThermalMonitor extends TileEntityInventory implements ITilePacketHandler {
	private int heatLevel;
	private boolean invertRedstone;
	protected int status;
	private boolean poweredBlock;

	protected int updateTicker;
	protected int tickRate;

	public TileEntityThermalMonitor(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		invertRedstone = false;
		heatLevel = 500;
		updateTicker = 0;
		tickRate = ConfigHandler.THERMAL_MONITOR_REFRESH_PERIOD.get();
		status = -1;
	}

	public TileEntityThermalMonitor(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.thermal_monitor.get(), pos, state);
	}

	public int getHeatLevel() {
		return heatLevel;
	}

	public void setHeatLevel(int value) {
		int old = heatLevel;
		heatLevel = value;
		if (!level.isClientSide && heatLevel != old)
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
	public void onServerMessageReceived(CompoundTag tag) {
		if (!tag.contains("type"))
			return;
		switch (tag.getInt("type")) {
		case 1:
			if (tag.contains("value"))
				setHeatLevel(tag.getInt("value"));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getInt("value") == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

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
		tag.putInt("status", status);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(CompoundTag tag) {
		super.readProperties(tag);
		if (tag.contains("heatLevel"))
			heatLevel = tag.getInt("heatLevel");
		if (tag.contains("invert"))
			invertRedstone = tag.getBoolean("invert");
		if (tag.contains("status"))
			setStatus(tag.getInt("status"));
		if (tag.contains("poweredBlock"))
			poweredBlock = tag.getBoolean("poweredBlock");
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundTag writeProperties(CompoundTag tag) {
		tag = super.writeProperties(tag);
		tag.putInt("heatLevel", heatLevel);
		tag.putBoolean("invert", invertRedstone);
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
		if (!(be instanceof TileEntityThermalMonitor))
			return;
		TileEntityThermalMonitor te = (TileEntityThermalMonitor) be;
		te.tick();
	}

	protected void tick() {
		if (level.isClientSide)
			return;
	
		if (updateTicker-- > 0)
			return;
		updateTicker = tickRate;
		checkStatus();
	}

	protected void checkStatus() {
		int heat = CrossModLoader.getReactorHeat(level, worldPosition);
		int newStatus = heat == -1 ? -2 : heat >= heatLevel ? 1 : 0;

		if (newStatus != status) {
			status = newStatus;
			notifyBlockUpdate();
			level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		}
	}

	public void notifyBlockUpdate() {
		BlockState iblockstate = level.getBlockState(worldPosition);
		Block block = iblockstate.getBlock();
		if (block instanceof ThermalMonitor || block instanceof RemoteThermalMonitor) {
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
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

	// ------- Inventory ------- 
	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return false;
	}
}
