package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.containers.ContainerRemoteThermalMonitor;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.init.ModTileEntityTypes;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.slots.ISlotItemFilter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityRemoteThermalMonitor extends TileEntityThermalMonitor implements ISlotItemFilter, INamedContainerProvider {
	public static final int SLOT_CARD = 0;
	public static final byte SLOT_UPGRADE_RANGE = 1;
	private static final int LOCATION_RANGE = 8;
	private int heat;

	public TileEntityRemoteThermalMonitor(TileEntityType<?> type) {
		super(type);
		heat = 0;
	}

	public TileEntityRemoteThermalMonitor() {
		this(ModTileEntityTypes.remote_thermo.get());
	}

	public int getHeat() {
		return heat;
	}

	@Override
	protected void readProperties(CompoundNBT tag) {
		super.readProperties(tag);
		if (tag.contains("heat"))
			heat = tag.getInt("heat");
	}

	@Override
	protected CompoundNBT writeProperties(CompoundNBT tag) {
		tag = super.writeProperties(tag);
		tag.putInt("heat", heat);
		return tag;
	}

	@Override
	protected void checkStatus() {
		int newStatus = -2;
		int newHeat = 0;

		if (!getItem(SLOT_CARD).isEmpty()) {
			BlockPos target = new ItemCardReader(getItem(SLOT_CARD)).getTarget();
			if (target != null) {
				int upgradeCountRange = 0;
				ItemStack stack = getItem(SLOT_UPGRADE_RANGE);
				if (!stack.isEmpty() && stack.getItem().equals(ModItems.upgrade_range.get()))
					upgradeCountRange = stack.getCount();
				int range = LOCATION_RANGE * (int) Math.pow(2, upgradeCountRange);
				if (Math.abs(target.getX() - worldPosition.getX()) <= range && Math.abs(target.getY() - worldPosition.getY()) <= range && Math.abs(target.getZ() - worldPosition.getZ()) <= range) {
					newHeat = CrossModLoader.getReactorHeat(level, target);
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
			level.updateNeighborsAt(worldPosition, level.getBlockState(worldPosition).getBlock());
		}
	}

	// Inventory
	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return isItemValid(index, stack);
	}

	@Override
	public boolean isItemValid(int slotIndex, ItemStack stack) { // ISlotItemFilter
		if (stack.isEmpty())
			return false;
		switch (slotIndex) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem().equals(ModItems.upgrade_range.get());
		default:
			return false;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public double getViewDistance() {
		return 65536.0D;
	}

	// INamedContainerProvider
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerRemoteThermalMonitor(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModItems.remote_thermo.get().getDescriptionId());
	}
}
