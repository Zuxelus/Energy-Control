package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotArmor;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;

public class ContainerAFSU extends ContainerBase<TileEntityAFSU> {
	private double lastEnergy = -1;

	public ContainerAFSU(EntityPlayer player, TileEntityAFSU te) {
		super(te);

		addSlotToContainer(new SlotFilter(te, TileEntityAFSU.SLOT_CHARGER, 26, 17)); // chargeSlot
		addSlotToContainer(new SlotFilter(te, TileEntityAFSU.SLOT_DISCHARGER, 26, 53)); // dischargeSlot
		for (int col = 0; col < 4; col++)
			addSlotToContainer((Slot) new SlotArmor(player.inventory, col, 8 + col * 18, 84));
		// inventory
		addPlayerInventorySlots(player, 196);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		for (int i = 0; i < crafters.size(); i++)
			if (lastEnergy != energy)
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, 1, energy);
		lastEnergy = energy;
	}
}