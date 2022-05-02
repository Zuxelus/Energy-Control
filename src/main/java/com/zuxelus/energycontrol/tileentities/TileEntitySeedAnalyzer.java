package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;
import com.zuxelus.zlib.tileentities.IBlockHorizontal;
import com.zuxelus.zlib.tileentities.ITilePacketHandler;
import com.zuxelus.zlib.tileentities.TileEntityInventory;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.Ic2Items;
import ic2.core.item.ItemCropSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySeedAnalyzer extends TileEntityInventory implements ITilePacketHandler, ISlotItemFilter, IEnergySink, IBlockHorizontal {
	public static final byte SLOT_IN = 0;
	public static final byte SLOT_OUT = 1;
	public static final byte SLOT_DISCHARGER = 2;

	private static final double CONSUMPTION = 5;
	public static final int TIER = 1;
	public static final int CAPACITY = 10000;
	public static final int[] COST_TO_UPGRADE = {10, 90, 900, 9000};

	private double energy;
	private double production;
	private int productionMax;
	private boolean addedToEnet;
	private boolean active;

	public TileEntitySeedAnalyzer() {
		super("tile.seed_analyzer.name");
		addedToEnet = false;
		active = false;
		energy = 0;
		production = 0;
		productionMax = 0;
	}

	public double getEnergy() {
		return energy;
	}

	public int getEnergyFactor() {
		return (int) Math.round(energy / CAPACITY * 14);
	}

	public double getProduction() {
		return production;
	}

	public int getProductionMax() {
		return productionMax;
	}

	public int getProductionFactor() {
		return (int) Math.round(production / productionMax * 24);
	}

	public boolean getActive() {
		return active;
	}

	@Override
	public void onServerMessageReceived(NBTTagCompound tag) { }

	@Override
	public void onClientMessageReceived(NBTTagCompound tag) {
		if (!tag.hasKey("type"))
			return;
		switch (tag.getInteger("type")) {
		case 1:
			if (tag.hasKey("energy") && tag.hasKey("production") && tag.hasKey("productionMax")) {
				energy = tag.getDouble("energy");
				production = tag.getDouble("production");
				productionMax = tag.getInteger("productionMax");
			}
			break;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		tag = writeProperties(tag);
		tag.setBoolean("active", active);
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
		if (tag.hasKey("energy"))
			energy = tag.getDouble("energy");
		if (tag.hasKey("production"))
			production = tag.getDouble("production");
		if (tag.hasKey("productionMax"))
			productionMax = tag.getInteger("productionMax");
		if (tag.hasKey("active")) {
			boolean old = active;
			active = tag.getBoolean("active");
			if (worldObj.isRemote && active != old)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readProperties(tag);
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setDouble("energy", energy);
		tag.setDouble("production", production);
		tag.setInteger("productionMax", productionMax);
		return tag;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeProperties(tag);
	}

	public void onLoad() {
		if (!addedToEnet && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnet = true;
			updateActive();
		}
	}

	@Override
	public void invalidate() {
		onChunkUnload();
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		if (addedToEnet && !worldObj.isRemote && Info.isIc2Available()) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnet = false;
		}
	}

	@Override
	public void updateEntity() {
		if (!addedToEnet)
			onLoad();
		if (worldObj.isRemote)
			return;
		ItemStack stack = getStackInSlot(SLOT_DISCHARGER);
		if (stack != null && energy < CAPACITY && stack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) stack.getItem();
			double old = energy;
			if (ielectricitem.canProvideEnergy(stack))
				energy += ElectricItem.manager.discharge(stack, CAPACITY - energy, TIER, false, false, false);
			if (old == 0 && energy >= CONSUMPTION)
				updateState();
		}
		if (!active)
			return;
		if (energy < CONSUMPTION) {
			energy = 0;
			production = 0;
			updateState();
			return;
		}
		if (productionMax > 0) {
			energy -= CONSUMPTION;
			production += CONSUMPTION;
			if (production >= productionMax) {
				ItemCropSeed.incrementScannedOfStack(inventory[SLOT_IN]);
				inventory[SLOT_OUT] = inventory[SLOT_IN];
				setInventorySlotContents(SLOT_IN, null);
				production = 0;
				productionMax = 0;
				updateState();
			}
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (worldObj == null || worldObj.isRemote)
			return;
		updateState();
	}

	private void updateActive() {
		active = false;
		productionMax = 0;
		if (energy < CONSUMPTION)
			return;
		ItemStack out = getStackInSlot(SLOT_OUT);
		if (out != null)
			return;
		ItemStack stack = getStackInSlot(SLOT_IN);
		if (stack == null || stack.getItem() != Ic2Items.cropSeed.getItem())
			return;
		int level = ItemCropSeed.getScannedFromStack(stack);
		if (level == 4) {
			inventory[SLOT_OUT] = inventory[SLOT_IN];
			setInventorySlotContents(SLOT_IN, null);
			return;
		}
		productionMax = COST_TO_UPGRADE[level];
		active = true;
	}

	private void updateState() {
		boolean old = active;
		updateActive();
		if (active == old)
			return;

		production = 0;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	// ------- Inventory -------
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_IN:
			return stack.getItem() == Ic2Items.cropSeed.getItem();
		case SLOT_DISCHARGER:
			return stack.getItem() instanceof IElectricItem;
		case SLOT_OUT:
		default:
			return false;
		}
	}

	// ISidedInventory
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {0, 1, 2};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		if (slot != SLOT_IN || side != 1)
			return false;
		return stack.getItem() == Ic2Items.cropSeed.getItem();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		if (slot != SLOT_OUT || side == 1)
			return false;
		ItemStack crop = getStackInSlot(slot);
		if (crop == null || crop.getItem() != Ic2Items.cropSeed.getItem())
			return false;
		if (side == 0)
			return ItemCropSeed.getScannedFromStack(crop) == 4;
		if (side != 0)
			return ItemCropSeed.getScannedFromStack(crop) < 4;
		return true;
	}

	// IEnergySink
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return Math.max(0, CAPACITY - energy);
	}

	@Override
	public int getSinkTier() {
		return TIER;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		double old = energy;
		energy += amount;
		double left = 0.0;

		if (energy > CAPACITY) {
			left = energy - CAPACITY;
			energy = CAPACITY;
		}
		if (energy >= CONSUMPTION && old == 0 && !worldObj.isRemote)
			updateState();
		return left;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockSeedAnalyzer);
	}
}
