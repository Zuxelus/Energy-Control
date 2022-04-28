package com.zuxelus.energycontrol.tileentities;

import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import micdoodle8.mods.galacticraft.api.power.EnergySource;
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC", modid = "galacticraftcore"),
	@Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical", modid = "galacticraftcore")
})
public class TileEntityAFSU extends TileEntityEnergyStorage implements ITickable, IEnergyStorage, IEnergyHandlerGC, IElectrical {
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
		if (worldObj != null && !worldObj.isRemote && redstoneMode != old) 
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
		boolean redstone = worldObj.isBlockPowered(pos);
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
		if (worldObj.isRemote)
			return;

		onLoad();
		handleDischarger(SLOT_DISCHARGER);
		handleCharger(SLOT_CHARGER);
		updateState();
	}

	public void updateState() {
		boolean old = poweredBlock;
		poweredBlock = shouldEmitRedstone();
		if (!worldObj.isRemote && (poweredBlock != old || allowEmit != shouldEmitEnergy())) {
			IBlockState state = worldObj.getBlockState(pos);
			if (poweredBlock != old)
				worldObj.notifyNeighborsOfStateChange(pos, state.getBlock());
			worldObj.notifyBlockUpdate(pos, state, state, 2);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
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
		return (item.canProvideEnergy(stack) || slot == SLOT_CHARGER) && item.getTier(stack) <= TIER;
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

	// IEnergyHandlerGC
	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float receiveEnergyGC(EnergySource from, float amount, boolean simulate) {
		float energyReceived = (float) Math.min(capacity - energy, amount);
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float extractEnergyGC(EnergySource from, float amount, boolean simulate) {
		return 0;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public boolean nodeAvailable(EnergySource from) {
		return true;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float getEnergyStoredGC(EnergySource from) {
		return (float) energy;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float getMaxEnergyStoredGC(EnergySource from) {
		return (float) capacity;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public boolean canConnect(EnumFacing direction, NetworkType type) {
		return direction != facing;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float receiveElectricity(EnumFacing from, float receive, int tierProduced, boolean doReceive) {
		if (from == facing)
			return 0;
		return receiveEnergyGC(null, receive, !doReceive);
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float provideElectricity(EnumFacing from, float request, boolean doProvide) {
		return extractEnergyGC(null, request, !doProvide);
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float getRequest(EnumFacing direction) {
		return (float) (capacity - energy);
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public float getProvide(EnumFacing direction) {
		return 0;
	}

	@Override
	@Optional.Method(modid = "galacticraftcore")
	public int getTierGC() {
		return 3;
	}
}
