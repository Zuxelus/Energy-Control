package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityRemoteThermo extends TileEntityThermo implements ISlotItemFilter {
	public static final int SLOT_CHARGER = 0;
	public static final int SLOT_CARD = 1;
	public static final byte SLOT_UPGRADE_RANGE = 2;
	private static final int LOCATION_RANGE = 8;
	private int heat;

	public TileEntityRemoteThermo() {
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

		if (!getStackInSlot(SLOT_CARD).isEmpty()) {
			BlockPos target = new ItemCardReader(getStackInSlot(SLOT_CARD)).getTarget();
			if (target != null) {
				int upgradeCountRange = 0;
				ItemStack stack = getStackInSlot(SLOT_UPGRADE_RANGE);
				if (!stack.isEmpty() && stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE)
					upgradeCountRange = stack.getCount();
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(target.getX() - pos.getX()) <= range && Math.abs(target.getY() - pos.getY()) <= range && Math.abs(target.getZ() - pos.getZ()) <= range) {
					newHeat = CrossModLoader.getReactorHeat(world, target);
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
			world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
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
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		if (stack.isEmpty())
			return false;
		switch (slotIndex) {
		case SLOT_CHARGER:
			return false;
		case SLOT_CARD:
			return stack.getItem() instanceof ItemCardMain && (stack.getItemDamage() == ItemCardType.CARD_REACTOR
					|| stack.getItemDamage() == ItemCardType.CARD_REACTOR5X5
					|| stack.getItemDamage() == ItemCardType.CARD_BIG_REACTORS
					|| stack.getItemDamage() == ItemCardType.CARD_HBM);
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
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
}
