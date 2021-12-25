package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContainerKitAssembler extends ContainerBase<TileEntityKitAssembler> {
	private double lastEnergy = -1;
	private double lastProduction = -1;

	public ContainerKitAssembler(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityKitAssembler) getBlockEntity(inventory, data));
	}

	public ContainerKitAssembler(int windowId, PlayerInventory inventory, TileEntityKitAssembler te) {
		super(te, ModContainerTypes.kit_assembler, windowId, ModItems.kit_assembler, ScreenHandlerContext.create(te.getWorld(), te.getPos()));
		// info card
		addSlot(new SlotCard(te, 0, 8, 17));

		addSlot(new SlotFilter(te, 1, 62, 17));
		addSlot(new SlotFilter(te, 2, 62, 17 + 18));
		addSlot(new SlotFilter(te, 3, 62, 17 + 18 * 2));
		addSlot(new SlotFilter(te, 4, 121, 35));

		addSlot(new SlotDischargeable(te, 5, 8, 17 + 18 * 2));
		// inventory
		addPlayerInventorySlots(inventory, 166);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (ServerPlayerEntity listener : listeners)
			if (lastEnergy != energy || lastProduction != production) {
				NbtCompound tag = new NbtCompound();
				tag.putInt("type", 1);
				tag.putDouble("energy", energy);
				tag.putDouble("production", production);
				tag.putDouble("production", production);
				tag.putInt("time", te.getRecipeTime());
				NetworkHelper.updateClientTileEntity(listener, te.getPos(), tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
