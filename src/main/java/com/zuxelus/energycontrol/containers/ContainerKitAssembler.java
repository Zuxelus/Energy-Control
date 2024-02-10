package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.network.NetworkHelper;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotDischargeable;
import com.zuxelus.zlib.containers.slots.SlotFilter;
import com.zuxelus.zlib.containers.slots.SlotTransformer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerKitAssembler extends ContainerBase<TileEntityKitAssembler> {
	private double lastEnergy = -1;
	private double lastProduction = -1;

	public ContainerKitAssembler(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory, (TileEntityKitAssembler) getBlockEntity(inventory, data));
	}

	public ContainerKitAssembler(int windowId, Inventory inventory, TileEntityKitAssembler te) {
		super(te, ModContainerTypes.kit_assembler.get(), windowId, ModItems.kit_assembler.get(), ContainerLevelAccess.create(te.getLevel(), te.getBlockPos()));
		// info card
		addSlot(new SlotCard(te, 0, 8, 17));

		addSlot(new SlotFilter(te, 1, 62, 17));
		addSlot(new SlotFilter(te, 2, 62, 17 + 18));
		addSlot(new SlotFilter(te, 3, 62, 17 + 18 * 2));
		addSlot(new SlotFilter(te, 4, 121, 35));

		addSlot(new SlotDischargeable(te, 5, 8, 17 + 18 * 2));
		addSlot(new SlotTransformer(te, 6, 8, 17 + 18));
		// inventory
		addPlayerInventorySlots(inventory, 166);
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		double energy = te.getEnergy();
		double production = te.getProduction();
		for (ServerPlayer listener : listeners)
			if (lastEnergy != energy || lastProduction != production) {
				CompoundTag tag = new CompoundTag();
				tag.putInt("type", 1);
				tag.putDouble("energy", energy);
				tag.putDouble("production", production);
				tag.putInt("time", te.getRecipeTime());
				NetworkHelper.updateClientTileEntity(listener, te.getBlockPos(), tag);
			}
		lastEnergy = energy;
		lastProduction = production;
	}
}
