package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerKitAssembler extends ContainerBase<TileEntityKitAssembler> {
	private double lastEnergy = -1;
	private double lastProduction = -1;

	public ContainerKitAssembler(EntityPlayer player, TileEntityKitAssembler te) {
		super(te);
		// info card
		addSlotToContainer(new SlotCard(te, 0, 8, 17));
		
		addSlotToContainer(new SlotFilter(te, 1, 62, 17));
		addSlotToContainer(new SlotFilter(te, 2, 62, 17 + 18));
		addSlotToContainer(new SlotFilter(te, 3, 62, 17 + 18 * 2));
		addSlotToContainer(new SlotFilter(te, 4, 121, 35));

		addSlotToContainer(new SlotDischargeable(te, 5, 8, 17 + 18 * 2, 1));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (IContainerListener listener : listeners)
			if (lastEnergy != energy || lastProduction != production) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("energy", energy);
				tag.setDouble("production", production);
				tag.setDouble("production", production);
				tag.setInteger("time", te.getRecipeTime());
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
