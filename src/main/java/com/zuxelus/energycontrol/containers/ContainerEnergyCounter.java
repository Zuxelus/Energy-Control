package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;

public class ContainerEnergyCounter extends ContainerBase<TileEntityEnergyCounter>
{
	private double lastCounter = -1;

	public ContainerEnergyCounter(EntityPlayer player, TileEntityEnergyCounter energyCounter)
	{
		super(energyCounter);
		// transformer upgrades
		addSlotToContainer(new SlotFilter(energyCounter, 0, 8, 18));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	/*@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		NetworkHelper.sendEnergyCounterValue(te, listener);
	}*/

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double counter = te.counter;
		for (int i = 0; i < listeners.size(); i++)
			if (lastCounter != counter)
				NetworkHelper.sendEnergyCounterValue(te, listeners.get(i));
		lastCounter = counter;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
		te.markDirty();
		return stack;
	}
}