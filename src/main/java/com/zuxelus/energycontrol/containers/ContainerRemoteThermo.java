package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.tileentities.TileEntityRemoteThermo;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerRemoteThermo extends ContainerBase<TileEntityRemoteThermo>
{
	private double lastEnergy = -1;

	public ContainerRemoteThermo(EntityPlayer player, TileEntityRemoteThermo remoteThermo)
	{
		super(remoteThermo);
		//energy charger
		addSlotToContainer(new SlotFilter(remoteThermo, 0, 13, 53));
		//upgrades
		addSlotToContainer(new SlotCard(remoteThermo, 1, 190, 8));
		addSlotToContainer(new SlotFilter(remoteThermo, 2, 190, 26));
		addSlotToContainer(new SlotFilter(remoteThermo, 3, 190, 44));
		addSlotToContainer(new SlotFilter(remoteThermo, 4, 190, 62));
		// inventory
		addPlayerInventorySlots(player, 216, 166);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		int energy = (int)te.getEnergy();
		for (int i = 0; i < listeners.size(); i++)
			if (lastEnergy != energy)
				listeners.get(i).sendWindowProperty(this, 0, energy);
		lastEnergy = energy;
		te.setStatus(-1);
	}

	public void updateProgressBar(int type, int value)
	{
		if (type == 0)
			te.setEnergy(value);
	}
}
