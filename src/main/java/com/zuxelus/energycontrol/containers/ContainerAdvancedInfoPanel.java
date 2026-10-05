package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerAdvancedInfoPanel(int windowId, PlayerInventory inventory, PacketByteBuf data) {
		this(windowId, inventory, (TileEntityInfoPanel) getBlockEntity(inventory, data));
	}

	public ContainerAdvancedInfoPanel(int windowId, PlayerInventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel_advanced, windowId, ModItems.info_panel_advanced, ScreenHandlerContext.create(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerAdvancedInfoPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotCard(panel, 1, 8 + 18, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerAdvancedInfoPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotCard(panel, 2, 8 + 36, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void markDirty() {
				if (panel.getWorld().isClient)
					ContainerAdvancedInfoPanel.this.sendContentUpdates();
			};
		});
		addSlot(new SlotRange(panel, 3, 8 + 54, 24 + 18));
		// inventory
		addPlayerInventorySlots(inventory, 223);
	}
}
