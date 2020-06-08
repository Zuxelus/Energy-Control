package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import ic2.core.slot.ArmorSlot;
import ic2.core.slot.SlotArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerAFSU extends ContainerBase<TileEntityAFSU> {
	private double lastEnergy = -1;

	public ContainerAFSU(EntityPlayer player, TileEntityAFSU te) {
		super(te);

		addSlotToContainer(new SlotFilter(te, 0, 26, 17)); // chargeSlot
		addSlotToContainer(new SlotFilter(te, 1, 26, 53)); // dischargeSlot
		for (int col = 0; col < ArmorSlot.getCount(); col++)
			addSlotToContainer((Slot) new SlotArmor(player.inventory, ArmorSlot.get(col), 8 + col * 18, 84));
		// inventory
		addPlayerInventorySlots(player, 196);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		for (int i = 0; i < listeners.size(); i++)
			if (lastEnergy != energy)
				NetworkHelper.updateClientTileEntity(listeners.get(i), te.getPos(), 1, energy);
		lastEnergy = energy;
	}
}