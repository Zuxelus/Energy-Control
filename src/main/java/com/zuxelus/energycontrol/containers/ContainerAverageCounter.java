package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAverageCounter extends ContainerBase<TileEntityAverageCounter>
{
	private double lastAverage = -1;

	public ContainerAverageCounter(EntityPlayer player, TileEntityAverageCounter averageCounter) {
		super(averageCounter);
		// transformer upgrades
		addSlotToContainer(new SlotFilter(averageCounter, 0, 8, 18));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		NetworkHelper.sendAverageCounterValue(te, listener, te.getClientAverage());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int average = te.getClientAverage();
		for (int i = 0; i < listeners.size(); i++) {
			if (lastAverage != average)
				NetworkHelper.sendAverageCounterValue(te, listeners.get(i), average);
		}
		lastAverage = average;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {	
		ItemStack stack = super.slotClick(slotId, dragType, clickType, player);
		te.markDirty();
		return stack;
	}
}