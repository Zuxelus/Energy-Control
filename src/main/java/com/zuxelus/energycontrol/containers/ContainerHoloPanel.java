package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotPower;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;

public class ContainerHoloPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerHoloPanel(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityInfoPanel) getBlockEntity(inventory, data));
	}

	public ContainerHoloPanel(int windowId, PlayerInventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.holo_panel, windowId, ModItems.holo_panel, ScreenHandlerContext.create(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerHoloPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotPower(panel, 2, 8, 24 + 18 * 3));
		// inventory
		addPlayerInventorySlots(inventory, 201);
	}
}
