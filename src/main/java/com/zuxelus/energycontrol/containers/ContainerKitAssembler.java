package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerKitAssembler extends ContainerBase<TileEntityKitAssembler> {
	private double lastEnergy = -1;
	private double lastProduction = -1;

	public ContainerKitAssembler(EntityPlayer player, TileEntityKitAssembler te) {
		super(te);
		// info card
		addSlotToContainer(new SlotCard(te, 0, 8, 42));
		
		addSlotToContainer(new SlotFilter(te, 1, 62, 42));
		addSlotToContainer(new SlotFilter(te, 2, 62, 42 + 18));
		addSlotToContainer(new SlotFilter(te, 3, 62, 42 + 18 * 2));
		addSlotToContainer(new SlotFilter(te, 4, 121, 60));
		// inventory
		addPlayerInventorySlots(player, 190);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (int i = 0; i < listeners.size(); i++)
			if (lastEnergy != energy || lastProduction != production) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("energy", energy);
				tag.setDouble("production", production);
				NetworkHelper.updateClientTileEntity(listeners.get(i), te.getPos(), tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
