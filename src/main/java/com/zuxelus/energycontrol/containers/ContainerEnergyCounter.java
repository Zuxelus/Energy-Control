package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class ContainerEnergyCounter extends ContainerBase<TileEntityEnergyCounter>
{
	private double lastCounter = -1;

	public ContainerEnergyCounter(EntityPlayer player, TileEntityEnergyCounter te)
	{
		super(te);
		// transformer upgrades
		addSlotToContainer(new SlotFilter(te, 0, 8, 18));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double counter = te.counter;
		for (int i = 0; i < crafters.size(); i++)
			if (lastCounter != counter)
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, 1, counter);
		lastCounter = counter;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, int clickTypeIn, EntityPlayer player) {
		ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
		te.markDirty();
		return stack;
	}
}