package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerAdvancedInfoPanel(int windowId, Inventory inventory, FriendlyByteBuf data) {
		this(windowId, inventory, (TileEntityInfoPanel) getBlockEntity(inventory, data));
	}

	public ContainerAdvancedInfoPanel(int windowId, Inventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel_advanced.get(), windowId, ModItems.info_panel_advanced.get(), ContainerLevelAccess.create(panel.getLevel(), panel.getBlockPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerAdvancedInfoPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotCard(panel, 1, 8 + 18, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerAdvancedInfoPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotCard(panel, 2, 8 + 36, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerAdvancedInfoPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotRange(panel, 3, 8 + 54, 24 + 18));
		// inventory
		addPlayerInventorySlots(inventory, 223);
	}
}
