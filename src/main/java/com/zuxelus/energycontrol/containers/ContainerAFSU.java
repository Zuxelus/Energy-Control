package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityAFSU;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotArmor;
import com.zuxelus.zlib.containers.slots.SlotChargeable;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;

public class ContainerAFSU extends ContainerBase<TileEntityAFSU> {
	private static final EntityEquipmentSlot[] armorSlots = getArmorSlots();
	private double lastEnergy = -1;

	public ContainerAFSU(EntityPlayer player, TileEntityAFSU te) {
		super(te);

		addSlotToContainer(new SlotChargeable(te, TileEntityAFSU.SLOT_CHARGER, 26, 17)); // chargeSlot
		addSlotToContainer(new SlotDischargeable(te, TileEntityAFSU.SLOT_DISCHARGER, 26, 53, TileEntityAFSU.TIER)); // dischargeSlot
		for (int col = 0; col < armorSlots.length; col++)
			addSlotToContainer((Slot) new SlotArmor(player.inventory, armorSlots[col], 8 + col * 18, 84));
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

	private static EntityEquipmentSlot[] getArmorSlots() {
		EntityEquipmentSlot[] values = EntityEquipmentSlot.values();
		int count = 0;
		for (EntityEquipmentSlot slot : values)
			if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
				count++;
		EntityEquipmentSlot[] ret = new EntityEquipmentSlot[count];
		int i;
		for (i = 0; i < ret.length; i++)
			for (EntityEquipmentSlot slot : values)
				if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && slot.getIndex() == i) {
					ret[i] = slot;
					break;
				}
		for (i = 0; i < ret.length; i++)
			if (ret[i] == null)
				throw new RuntimeException("Can't find an armor mapping for idx " + i);
		return ret;
	}
}