package com.zuxelus.energycontrol.containers;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerKitAssembler extends ContainerBase<TileEntityKitAssembler> {
	private double lastEnergy = -1;
	private double lastProduction = -1;
	private final List<IContainerListener> listeners = Lists.newArrayList();

	public ContainerKitAssembler(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityKitAssembler) getTileEntity(inventory, data));
	}

	public ContainerKitAssembler(int windowId, PlayerInventory inventory, TileEntityKitAssembler te) {
		super(te, ModContainerTypes.kit_assembler.get(), windowId, ModItems.kit_assembler.get(), IWorldPosCallable.of(te.getWorld(), te.getPos()));
		// info card
		addSlot(new SlotCard(te, 0, 8, 42));
		
		addSlot(new SlotFilter(te, 1, 62, 42));
		addSlot(new SlotFilter(te, 2, 62, 42 + 18));
		addSlot(new SlotFilter(te, 3, 62, 42 + 18 * 2));
		addSlot(new SlotFilter(te, 4, 121, 60));
		// inventory
		addPlayerInventorySlots(inventory, 190);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listeners.add(listener);
	}

	@Override
	public void removeListener(IContainerListener listener) {
		super.removeListener(listener);
		listeners.remove(listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (int i = 0; i < listeners.size(); i++)
			if (lastEnergy != energy || lastProduction != production) {
				CompoundNBT tag = new CompoundNBT();
				tag.putInt("type", 1);
				tag.putDouble("energy", energy);
				tag.putDouble("production", production);
				NetworkHelper.updateClientTileEntity(listeners.get(i), te.getPos(), tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
