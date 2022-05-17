package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TileEntityRemoteThermalMonitor extends TileEntityThermalMonitor implements ISlotItemFilter {
	public static final int SLOT_CHARGER = 0;
	public static final int SLOT_CARD = 1;
	public static final byte SLOT_UPGRADE_RANGE = 2;
	private static final int LOCATION_RANGE = 8;
	private int heat;

	public TileEntityRemoteThermalMonitor() {
		super();
		customName = "tile.remote_thermo.name";
		heat = 0;
	}

	public int getHeat() {
		return heat;
	}

	@Override
	protected void readProperties(NBTTagCompound tag) {
		super.readProperties(tag);
		if (tag.hasKey("heat"))
			heat = tag.getInteger("heat");
	}

	@Override
	protected NBTTagCompound writeProperties(NBTTagCompound tag) {
		tag = super.writeProperties(tag);
		tag.setInteger("heat", heat);
		return tag;
	}

	@Override
	protected void checkStatus() {
		int newStatus = -2;
		int newHeat = 0;

		if (getStackInSlot(SLOT_CARD) != null) {
			ChunkCoordinates target = new ItemCardReader(getStackInSlot(SLOT_CARD)).getTarget();
			if (target != null) {
				int upgradeCountRange = 0;
				ItemStack stack = getStackInSlot(SLOT_UPGRADE_RANGE);
				if (stack != null && stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
					upgradeCountRange = stack.stackSize;
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(target.posX - xCoord) <= range && Math.abs(target.posY - yCoord) <= range && Math.abs(target.posZ - zCoord) <= range) {
					newHeat = CrossModLoader.getHeat(worldObj, target.posX, target.posY, target.posZ);
					newStatus = newHeat == -1 ? -2 : newHeat >= getHeatLevel() ? 1 : 0;
					if (newHeat == -1)
						newHeat = 0;
				}
			}
		}

		if (newStatus != status || newHeat != heat) {
			status = newStatus;
			heat = newHeat;
			notifyBlockUpdate();
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		}
	}

	// Inventory
	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) {
		if (stack == null)
			return false;
		switch (slotIndex) {
		case SLOT_CHARGER:
			return false;
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain;
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		default:
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldBlock != newBlock;
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockRemoteThermo);
	}
}
