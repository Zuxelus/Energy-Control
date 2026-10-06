package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.RemoteThermalMonitor;
import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.TileEntityInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
		tickRate = -1;
		status = -1;
	}

	public TileEntityThermalMonitor(BlockPos pos, BlockState state) {
		this(ModTileEntityTypes.thermal_monitor, pos, state);
	}

	public int getHeatLevel() {
		return heatLevel;
	}

	public void setHeatLevel(int value) {
		int old = heatLevel;
		heatLevel = value;
		if (!level.isClientSide() && heatLevel != old)
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
		switch (tag.getIntOr("type", 0)) {
		case 1:
			if (tag.contains("value"))
				setHeatLevel(tag.getIntOr("value", 0));
			break;
		case 2:
			if (tag.contains("value"))
				setInvertRedstone(tag.getIntOr("value", 0) == 1);
			break;
		}
	}

	@Override
	public void onClientMessageReceived(CompoundTag tag) { }

	@Override
	protected void writeUpdateData(ValueOutput tag) {
		tag.putInt("status", status);
		tag.putBoolean("poweredBlock", poweredBlock);
	}

	@Override
	protected void readProperties(ValueInput tag) {
		super.readProperties(tag);
		heatLevel = tag.getIntOr("heatLevel", heatLevel);
		invertRedstone = tag.getBooleanOr("invert", invertRedstone);
		tag.getInt("status").ifPresent(this::setStatus);
		poweredBlock = tag.getBooleanOr("poweredBlock", poweredBlock);
	}

	@Override
	protected void writeProperties(ValueOutput tag) {
		super.writeProperties(tag);
		tag.putInt("heatLevel", heatLevel);
		tag.putBoolean("invert", invertRedstone);
	}

	@Override
	public void setRemoved() {
		// also called while the chunk unloads; touching the world then loads the chunk again and never finishes unloading
		if (Screen.isLoaded(level, worldPosition))
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
		if (level.isClientSide())
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

	@Override
	protected void notifyBlockUpdate() {
		BlockState state = level.getBlockState(worldPosition);
		Block block = state.getBlock();
		if (block instanceof ThermalMonitor || block instanceof RemoteThermalMonitor) {
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				level.updateNeighborsAt(worldPosition, block);
			}
			level.sendBlockUpdated(worldPosition, state, state, 2);
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
