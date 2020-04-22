package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.containers.ISlotItemFilter;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.utils.ReactorHelper;

import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.reactor.IReactor;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityRemoteThermo extends TileEntityThermo implements IEnergySink, ISlotItemFilter, IInventory {
	public static final int SLOT_CHARGER = 0;
	public static final int SLOT_CARD = 1;
	private static final double BASE_PACKET_SIZE = 32.0D;
	private static final int BASE_STORAGE = 600;
	private static final int STORAGE_PER_UPGRADE = 10000;
	private static final int ENERGY_SU_BATTERY = 1000;
	private static final int LOCATION_RANGE = 8;
	
	private int deltaX;
	private int deltaY;
	private int deltaZ;
	private double prevMaxStorage;
	private double maxStorage;
	private double prevMaxPacketSize;
	private double maxPacketSize;
	private int prevTier;
	private int tier;
	
	private double energy;
	
	public TileEntityRemoteThermo() {
		super();
		maxStorage = BASE_STORAGE;
		maxPacketSize = BASE_PACKET_SIZE;
		tier = 1;
		deltaX = 0;
		deltaY = 0;
		deltaZ = 0;
		energy = 0;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double value) {
		energy = value;
	}

	public void setTier(int value) {
		tier = value;
		if (!world.isRemote && tier != prevTier)
			notifyBlockUpdate();
		prevTier = tier;
	}
	
	public void setMaxPacketSize(double value) {
		maxPacketSize = value;
		if (!world.isRemote && maxPacketSize != prevMaxPacketSize)
			notifyBlockUpdate();
		prevMaxPacketSize = maxPacketSize;
	}
	
	public double getMaxStorage() {
		return maxStorage;
	}

	public void setMaxStorage(double value) {
		maxStorage = value;
		if (!world.isRemote && maxStorage != prevMaxStorage)
			notifyBlockUpdate();
		prevMaxStorage = maxStorage;
	}
	
	@Override
	protected void checkStatus() {
		markDirty();

		int newStatus;
		if (energy >= EnergyControl.config.remoteThermalMonitorEnergyConsumption) {
			IReactor reactor = ReactorHelper.getReactorAt(world, new BlockPos(pos.getX() + deltaX, pos.getY() + deltaY, pos.getZ() + deltaZ));
			if (reactor == null) {
				if (!getStackInSlot(SLOT_CARD).isEmpty()) {
					BlockPos target = new ItemCardReader(getStackInSlot(SLOT_CARD)).getTarget();
					if (target != null)
						reactor = ReactorHelper.getReactor3x3(world, target);
				}
			}

			if (reactor != null) {
				if (tickRate == -1) {
					tickRate = reactor.getTickRate() / 2;
					if (tickRate == 0)
						tickRate = 1;
					updateTicker = tickRate;
				}
				newStatus = reactor.getHeat();
			} else
				newStatus = -1;
		} else
			newStatus = -2;

		if (newStatus != status) {
			status = newStatus;
			notifyBlockUpdate();
			world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
		}
	}
	
	@Override
	public void update() {
		super.update();
		if (world.isRemote)
			return;
		//If is server
		int consumption = EnergyControl.config.remoteThermalMonitorEnergyConsumption;
		if (!getStackInSlot(SLOT_CHARGER).isEmpty() && energy < maxStorage) {
			if (getStackInSlot(SLOT_CHARGER).getItem() instanceof IElectricItem) {
				IElectricItem ielectricitem = (IElectricItem) getStackInSlot(SLOT_CHARGER).getItem();

				if (ielectricitem.canProvideEnergy(getStackInSlot(SLOT_CHARGER))) {
					double k = ElectricItem.manager.discharge(getStackInSlot(SLOT_CHARGER), maxStorage - energy, tier, false, false, false);
					energy += k;
				}
			} else if (Item.getIdFromItem(getStackInSlot(SLOT_CHARGER).getItem()) == Item.getIdFromItem((IC2Items.getItem("suBattery")).getItem())){
				if (ENERGY_SU_BATTERY <= maxStorage - energy || energy == 0) {
					getStackInSlot(SLOT_CHARGER).shrink(1);

					if (getStackInSlot(SLOT_CHARGER).getCount() <= 0)
						setInventorySlotContents(SLOT_CHARGER, ItemStack.EMPTY);

					energy += ENERGY_SU_BATTERY;
					if (energy > maxStorage)
						energy = maxStorage;
				}
			}
		}
		
		if (energy >= consumption) {
			energy -= consumption;
		} else 
			energy = 0;
		setEnergy(energy);
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		int upgradeCountTransormer = 0;
		int upgradeCountStorage = 0;
		int upgradeCountRange = 0;
		for (int i = 2; i < 5; i++) {
			ItemStack itemStack = getStackInSlot(i);

			if (itemStack.isEmpty())
				continue;

			if (itemStack.isItemEqual(IC2Items.getItem("upgrade","transformer"))) {
				upgradeCountTransormer += itemStack.getCount();
			} else if (itemStack.isItemEqual(IC2Items.getItem("upgrade","energy_storage"))) {
				upgradeCountStorage += itemStack.getCount();
			} else if (itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE) {
				upgradeCountRange += itemStack.getCount();
			}
		}
		if (!getStackInSlot(SLOT_CARD).isEmpty()) {
			BlockPos target = new ItemCardReader(getStackInSlot(SLOT_CARD)).getTarget();
			if (target != null) {
				deltaX = target.getX() - pos.getX();
				deltaY = target.getY() - pos.getY();
				deltaZ = target.getZ() - pos.getZ();
				if (upgradeCountRange > 7)
					upgradeCountRange = 7;
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(deltaX) > range || Math.abs(deltaY) > range || Math.abs(deltaZ) > range)
					deltaX = deltaY = deltaZ = 0;
			} else {
				deltaX = 0;
				deltaY = 0;
				deltaZ = 0;
			}
		} else {
			deltaX = 0;
			deltaY = 0;
			deltaZ = 0;
			status = -2;
		}
		upgradeCountTransormer = Math.min(upgradeCountTransormer, 4);
		if (world != null && !world.isRemote) {
			tier = upgradeCountTransormer + 1;
			setTier(tier);
			maxPacketSize = BASE_PACKET_SIZE * Math.pow(4D, upgradeCountTransormer);
			setMaxPacketSize(maxPacketSize);
			maxStorage = BASE_STORAGE + STORAGE_PER_UPGRADE * upgradeCountStorage;
			setMaxStorage(maxStorage);
			if (energy > maxStorage)
				energy = maxStorage;
			setEnergy(energy);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);		
		energy = tag.getDouble("energy");
		markDirty();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("energy", energy);
		return tag;
	}
	
	// Inventory
	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack itemStack) {
		if (itemStack.isEmpty())
			return false;
		switch (slotIndex) {
		case SLOT_CHARGER:
			if (itemStack.getItem() instanceof IElectricItem) {
				IElectricItem item = (IElectricItem) itemStack.getItem();
				if (item.canProvideEnergy(itemStack) && item.getTier(itemStack) <= tier)
					return true;
			}
			return false;
		case SLOT_CARD:
			return itemStack.getItem() instanceof ItemCardMain && itemStack.getItemDamage() == ItemCardType.CARD_REACTOR;
		default:
			return itemStack.isItemEqual(IC2Items.getItem("upgrade","transformer"))
					|| itemStack.isItemEqual(IC2Items.getItem("upgrade","energy_storage"))
					|| (itemStack.getItem() instanceof ItemUpgrade && itemStack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE);
		}
	}
	
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter arg0, EnumFacing arg1) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return maxStorage - energy;
	}

	@Override
	public int getSinkTier() {
		return tier;
	}

	@Override
	public double injectEnergy(EnumFacing arg0, double amount, double voltage) {
		energy += amount;
		double left = 0.0;

		if (energy > maxStorage) {
			left = energy - maxStorage;
			energy = maxStorage;
		}
		setEnergy(energy);
		return left;
	}
}
