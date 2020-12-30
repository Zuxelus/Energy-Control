package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.AFSU;
import com.zuxelus.energycontrol.blocks.BlockDamages;
import com.zuxelus.energycontrol.blocks.RangeTrigger;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityEnergyStorage;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAFSU extends TileEntityEnergyStorage implements IEnergySource, IWrenchable {
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
		boolean redstone = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
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
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		allowEmit = shouldEmitEnergy();
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		if (!worldObj.isRemote)
			return;
		readProperties(pkt.func_148857_g());
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
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	@Override
	public void updateEntity() {
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
			Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
			if (poweredBlock != old)
				worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, block);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	private void notifyBlockUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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

	// IEnergySource
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection dir) {
		return dir == getFacingForge();
	}

	@Override
	public void drawEnergy(double amount) {
		energy -= amount;
	}

	@Override
	public double getOfferedEnergy() {
		return allowEmit ? energy >= output ? output : 0.0D : 0.0D; 
	}

	@Override
	public int getSourceTier() {
		return tier;
	}

	// IEnergyStorage
	@Override
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return true;
	}

	// IWrenchable
	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side) {
		return facing.ordinal() != side;
	}

	@Override
	public short getFacing() {
		return (short) facing.ordinal();
	}

	@Override
	public void setFacing(short facing) {
		setFacing((int) facing);
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public float getWrenchDropRate() {
		return 1;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer) {
		return AFSU.getStackwithEnergy(getEnergy() * 0.8D);
	}
}