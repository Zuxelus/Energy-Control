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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerInfoPanel(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityInfoPanel) getTileEntity(inventory, data));
	}

	public ContainerInfoPanel(int windowId, PlayerInventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.info_panel.get(), windowId, ModItems.info_panel.get(), IWorldPosCallable.create(panel.getLevel(), panel.getBlockPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerInfoPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotColor(panel, 2, 8, 24 + 18 * 3) {
			@SuppressWarnings("resource")
			@Override
			public void setChanged() {
				if (panel.getLevel().isClientSide)
					ContainerInfoPanel.this.broadcastChanges();
			};
		});
		addSlot(new SlotTouch(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(inventory, 201);
	}
}
