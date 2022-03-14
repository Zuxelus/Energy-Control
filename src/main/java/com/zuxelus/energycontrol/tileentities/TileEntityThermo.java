package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControlConfig;
import com.zuxelus.energycontrol.blocks.RemoteThermo;
import com.zuxelus.energycontrol.blocks.ThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.IC2ReactorHelper;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;
import ic2.api.reactor.IReactor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityThermo extends TileEntityInventory implements ITickable, ITilePacketHandler {
	private int heatLevel;
	private boolean invertRedstone;
	protected int status;
	private boolean poweredBlock;

	protected int updateTicker;
	protected int tickRate;

	public TileEntityThermo() {
		super("tile.thermal_monitor.name");
		invertRedstone = false;
		heatLevel = 500;
		updateTicker = 0;
		tickRate = EnergyControlConfig.thermalMonitorRefreshPeriod;
		status = -1;
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
		tag.setInteger("status", status);
		tag.setBoolean("poweredBlock", poweredBlock);
		return tag;
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void invalidate() {
		world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
		super.invalidate();
	}

	@Override
	public void update() {
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
			world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
		}
	}

	public void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		Block block = iblockstate.getBlock();
		if (block instanceof ThermalMonitor || block instanceof RemoteThermo) {
			boolean newValue = status < 0 ? false : status == 1 ? !invertRedstone : invertRedstone;
			if (poweredBlock != newValue) {
				poweredBlock = newValue;
				world.notifyNeighborsOfStateChange(pos, block, false);
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
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}
}
