package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotPower;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerHoloPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerHoloPanel(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory, (TileEntityInfoPanel) getBlockEntity(inventory, data));
	}

	public ContainerHoloPanel(int windowId, Inventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.holo_panel.get(), windowId, ModItems.holo_panel.get(), ContainerLevelAccess.create(panel.getLevel(), panel.getBlockPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerHoloPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotPower(panel, 2, 8, 24 + 18 * 3));
		// inventory
		addPlayerInventorySlots(inventory, 201);
	}
}
