package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.blocks.AFSU;
import com.zuxelus.energycontrol.crossmod.ModIDs;

import cpw.mods.fml.common.Optional;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import micdoodle8.mods.galacticraft.api.power.EnergySource;
import micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC;
import micdoodle8.mods.galacticraft.api.transmission.NetworkType;
import micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.InterfaceList({
	@Optional.Interface(modid = ModIDs.GALACTICRAFT_CORE, iface = "micdoodle8.mods.galacticraft.api.power.IEnergyHandlerGC"),
	@Optional.Interface(modid = ModIDs.GALACTICRAFT_CORE, iface = "micdoodle8.mods.galacticraft.api.transmission.tile.IElectrical")
})
public class TileEntityAFSU extends TileEntityEnergyStorage implements IEnergyStorage, IEnergyHandlerGC, IElectrical {
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
	public boolean isTeleporterCompatible(ForgeDirection side) {
		return true;
	}

	// IEnergyHandlerGC
	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float receiveEnergyGC(EnergySource from, float amount, boolean simulate) {
		float energyReceived = (float) Math.min(capacity - energy, amount);
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float extractEnergyGC(EnergySource from, float amount, boolean simulate) {
		return 0;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public boolean nodeAvailable(EnergySource from) {
		return true;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float getEnergyStoredGC(EnergySource from) {
		return (float) energy;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float getMaxEnergyStoredGC(EnergySource from) {
		return (float) capacity;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public boolean canConnect(ForgeDirection direction, NetworkType type) {
		return direction != facing;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float receiveElectricity(ForgeDirection from, float receive, int tierProduced, boolean doReceive) {
		if (from == facing)
			return 0;
		return receiveEnergyGC(null, receive, !doReceive);
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float provideElectricity(ForgeDirection from, float request, boolean doProvide) {
		return extractEnergyGC(null, request, !doProvide);
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float getRequest(ForgeDirection direction) {
		return (float) (capacity - energy);
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public float getProvide(ForgeDirection direction) {
		return 0;
	}

	@Override
	@Optional.Method(modid = ModIDs.GALACTICRAFT_CORE)
	public int getTierGC() {
		return 3;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return AFSU.getStackwithEnergy(getEnergy() * 0.8D);
	}
}