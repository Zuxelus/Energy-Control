package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.RemoteThermalMonitor;
import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.config.ConfigHandler;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityThermalMonitor extends TileEntityInventory implements ITickableTileEntity, ITilePacketHandler {
	private int heatLevel;
	private boolean invertRedstone;
	protected int status;
	private boolean poweredBlock;

	protected int updateTicker;
	protected int tickRate;

	public TileEntityThermalMonitor(TileEntityType<?> type) {
		super(type);
		invertRedstone = false;
		heatLevel = 500;
		updateTicker = 0;
		tickRate = ConfigHandler.THERMAL_MONITOR_REFRESH_PERIOD.get();
		status = -1;
	}

	public TileEntityThermalMonitor() {
		this(ModTileEntityTypes.thermal_monitor.get());
	}

	public int getHeatLevel() {
		return heatLevel;
	}

	public void setHeatLevel(int value) {
		int old = heatLevel;
		heatLevel = value;
		if (!world.isRemote && heatLevel != old)
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
	public void onServerMessageReceived(CompoundNBT tag) {
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
	public void onClientMessageReceived(CompoundNBT tag) { }

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
		tag.putInt("status", status);
		tag.putBoolean("poweredBlock", poweredBlock);
		return tag;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
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
	public void read(CompoundNBT tag) {
		super.read(tag);
		readProperties(tag);
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("heatLevel", heatLevel);
		tag.putBoolean("invert", invertRedstone);
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
	
		if (updateTicker-- > 0)
			return;
		updateTicker = tickRate;
		checkStatus();
	}

	protected void checkStatus() {
		int heat = CrossModLoader.getReactorHeat(world, pos);
		int newStatus = heat == -1 ? -2 : heat >= heatLevel ? 1 : 0;

		if (newStatus != status) {
			status = newStatus;
			notifyBlockUpdate();
			world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
		}
	}

	public void notifyBlockUpdate() {
		BlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (block instanceof ThermalMonitor || block instanceof RemoteThermalMonitor) {
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				world.notifyNeighborsOfStateChange(pos, block);
			}
			world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
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
	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}
}
