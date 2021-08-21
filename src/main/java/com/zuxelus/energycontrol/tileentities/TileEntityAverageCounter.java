package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.info.Info;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityAverageCounter extends TileEntityEnergyStorage implements ITickable {
	private static final int BASE_PACKET_SIZE = 32;
	protected static final int DATA_POINTS = 11 * 20;
	private boolean init;

	protected double[] data;
	protected int index;
	protected int updateTicker;
	protected int tickRate;

	protected short prevPeriod;
	public short period;
	protected int clientAverage = -1;

	public TileEntityAverageCounter() {
		super("tile.average_counter.name", 1, BASE_PACKET_SIZE, BASE_PACKET_SIZE * 2);
		data = new double[DATA_POINTS];
		index = 0;
		tickRate = 20;
		updateTicker = tickRate;
		prevPeriod = period = 1;
	}

	public int getClientAverage() {
		if (clientAverage == -1)
			return getAverage();
		return clientAverage;
	}

	private int getAverage() {
		int start = DATA_POINTS + index - period * 20;
		double sum = 0;
		for (int i = 0; i < period * 20; i++)
			sum += data[(start + i) % DATA_POINTS];
		clientAverage = (int) Math.round(sum / period / 20);
		return clientAverage;
	}

	private void setPeriod(short p) {
		period = p;
		if (!world.isRemote && prevPeriod != period)
			notifyBlockUpdate();
		prevPeriod = period;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
        if (tag.getInteger("type") == 1) {
            if (tag.hasKey("value")) {
                int event = tag.getInteger("value");
                if (event == 0) {
                    for (int i = 0; i < DATA_POINTS; i++)
                        data[i] = 0;

                    updateTicker = tickRate;
                    index = 0;
                } else
                    setPeriod((short) event);
            }
        }
	}

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
        if (tag.getInteger("type") == 1) {
            if (tag.hasKey("value"))
                clientAverage = tag.getInteger("value");
        }
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		return new SPacketUpdateTileEntity(getPos(), 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		index = tag.getInteger("dataIndex");
		updateTicker = tag.getInteger("updateTicker");
		prevPeriod = period = tag.getShort("period");

		for (int i = 0; i < DATA_POINTS; i++)
			data[i] = tag.getLong("point-" + i);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("dataIndex", index);
		tag.setInteger("updateTicker", updateTicker);
		tag.setShort("period", period);

		for (int i = 0; i < DATA_POINTS; i++)
			tag.setLong("point-" + i, (long) data[i]);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!init) {
			init = true;
			refreshData();
		}
	}

	@Override
	public void drawEnergy(double amount) {
		super.drawEnergy(amount);
		data[index] += amount;
	}

	@Override
	public void update() {
		if (world.isRemote)
			return;

		index = (index + 1) % DATA_POINTS;
		data[index] = 0;
		getAverage();
	}

	@Override
	public void markDirty() {
		super.markDirty();
		refreshData();
	}

	private void refreshData() {
		int upgradeCountTransormer = 0;
		ItemStack itemStack = getStackInSlot(0);
		if (!itemStack.isEmpty() && itemStack.isItemEqual(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("transformer")))
			upgradeCountTransormer = itemStack.getCount();
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 4);
		if (world != null && !world.isRemote) {
			output = BASE_PACKET_SIZE * (int) Math.pow(4D, upgradeCountTransormer);
			capacity = output * 2;
			tier = upgradeCountTransormer + 1;

			if (Info.isIc2Available()) {
				if (addedToEnet) {
					EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
					MinecraftForge.EVENT_BUS.post(event);
				}
				addedToEnet = false;
				EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
				MinecraftForge.EVENT_BUS.post(event);
				addedToEnet = true;
			}
		}
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		return stack.isItemEqual(CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("transformer"));
	}

	private void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	// IEnergySource
	@Override
	public double getOfferedEnergy() {
		return allowEmit ? energy >= output ? output : energy : 0.0D; 
	}
}
