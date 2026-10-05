package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotColor;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.containers.slots.SlotTouch;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerInfoPanel(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityInfoPanel) getBlockEntity(inventory, data));
	}

	public ContainerInfoPanel(int windowId, PlayerInventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel, windowId, ModItems.info_panel, ScreenHandlerContext.create(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerInfoPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotColor(panel, 2, 8, 24 + 18 * 3) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerInfoPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotTouch(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(inventory, 201);
	}
}
