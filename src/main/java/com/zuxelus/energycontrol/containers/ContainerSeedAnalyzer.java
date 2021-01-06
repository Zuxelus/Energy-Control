package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntitySeedAnalyzer;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import com.zuxelus.zlib.network.NetworkHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerSeedAnalyzer extends ContainerBase<TileEntitySeedAnalyzer> {
	private double lastEnergy = -1;
	private double lastProduction = -1;
	
	public ContainerSeedAnalyzer(EntityPlayer player, TileEntitySeedAnalyzer te) {
		super(te);
		
		addSlotToContainer(new SlotFilter(te, TileEntitySeedAnalyzer.SLOT_IN, 56, 17));
		addSlotToContainer(new SlotFilter(te, TileEntitySeedAnalyzer.SLOT_OUT, 116, 35));
		addSlotToContainer(new SlotFilter(te, TileEntitySeedAnalyzer.SLOT_DISCHARGER, 56, 53));
		// inventory
		addPlayerInventorySlots(player, 166);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (int i = 0; i < crafters.size(); i++)
			if (lastEnergy != energy || lastProduction != production) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("type", 1);
				tag.setDouble("energy", energy);
				tag.setDouble("production", production);
				tag.setInteger("productionMax", te.getProductionMax());
				NetworkHelper.updateClientTileEntity((ICrafting)crafters.get(i), te.xCoord, te.yCoord, te.zCoord, tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
