package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.RangeTrigger;
import com.zuxelus.energycontrol.containers.ISlotItemFilter;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityAFSU extends TileEntityEnergyStorage implements ITickable, ISlotItemFilter, ITilePacketHandler, IEnergyStorage {
	public static final int TIER = 5;
	public static final int CAPACITY = 400000000;
	public static final int OUTPUT = 8192;
	
	public static final int SLOT_CHARGER = 0;
	public static final int SLOT_DISCHARGER = 1;
	
	private byte redstoneMode;
	private boolean poweredBlock;

	public TileEntityAFSU() {
		super("tile.afsu.name", TIER, OUTPUT, CAPACITY);
		redstoneMode = 0;
	}

	public byte getRedstoneMode() {
		return redstoneMode;
	}

	public void setRedstoneMode(byte value) {
		byte old = redstoneMode;
		redstoneMode = value;
		if (world!= null && !world.isRemote && redstoneMode != old) 
			notifyBlockUpdate();
	}

	public boolean getPowered() {
		return poweredBlock;
	}

	protected boolean shouldEmitRedstone() {
		switch (redstoneMode) {
		case 1:
			return energy >= capacity - output * 20.0D;
		case 2:
			return energy > output && energy < capacity - output;
		case 3:
			return energy < capacity - output;
		case 4:
			return energy < output;
		}
		return false;
	}

	protected boolean shouldEmitEnergy() {
		boolean redstone = world.isBlockPowered(pos);
		if (redstoneMode == 5)
			return !redstone;
		if (redstoneMode == 6)
			return (!redstone || energy > capacity - output * 20.0D);
		return true;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) { 
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("value"))
				setRedstoneMode((byte) tag.getDouble("value"));
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
				energy = tag.getDouble("value");
			break;
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readProperties(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag = writeProperties(tag);
		allowEmit = shouldEmitEnergy();
		return tag;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		setRedstoneMode(tag.getByte("redstoneMode"));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setByte("redstoneMode", redstoneMode);
		return tag;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeProperties(super.writeToNBT(tag));
	}

	@Override
	public void update() {
		if (world.isRemote)
			return;
		ItemStack stack = getStackInSlot(SLOT_DISCHARGER);
		if (!stack.isEmpty() && energy < CAPACITY && stack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) stack.getItem();
			if (ielectricitem.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, CAPACITY - energy, TIER, false, false, false);
		}
		stack = getStackInSlot(SLOT_CHARGER);
		if (!stack.isEmpty() && energy > 0 && stack.getItem() instanceof IElectricItem) {
			IElectricItem item = (IElectricItem) stack.getItem();
			int tier = item.getTier(stack);
			double amount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, tier, true, true);
			amount = Math.min(amount, energy);
			if (amount > 0)
				energy -= ElectricItem.manager.charge(stack, amount, tier, false, false);
		}
		updateState();
	}

	public void updateState() {
		boolean old = poweredBlock;
		poweredBlock = shouldEmitRedstone();
		if (!world.isRemote && (poweredBlock != old || allowEmit != shouldEmitEnergy())) {
			IBlockState state = world.getBlockState(pos);
			if (poweredBlock != old)
				world.notifyNeighborsOfStateChange(pos, state.getBlock(), false);
			world.notifyBlockUpdate(pos, state, state, 2);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	private void notifyBlockUpdate() {
		IBlockState iblockstate = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, iblockstate, iblockstate, 2);
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) { // ISlotItemFilter:
		if (!(stack.getItem() instanceof IElectricItem))
				return false;
		IElectricItem item = (IElectricItem) stack.getItem();
		if ((item.canProvideEnergy(stack) || slot == SLOT_CHARGER) && item.getTier(stack) <= TIER)
			return true;
		return false;
	}

	// IEnergyStorage
	@Override
	public int getStored() {
		return (int) getEnergy();
	}

	@Override
	public void setStored(int energy) { }

	@Override
	public int addEnergy(int amount) {
		amount = (int) Math.min(capacity - energy, amount);
		energy += amount;
		return amount;
	}

	@Override
	public int getCapacity() {
		return (int) capacity;
	}

	@Override
	public double getOutputEnergyUnitsPerTick() {
		return OUTPUT;
	}

	@Override
	public boolean isTeleporterCompatible(EnumFacing side) {
		return true;
	}
}