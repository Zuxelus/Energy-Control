package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotPower;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModContainerTypes;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;

public class ContainerHoloPanel extends ContainerBase<TileEntityInfoPanel> {

	public ContainerHoloPanel(int windowId, PlayerInventory inventory, PacketBuffer data) {
		this(windowId, inventory, (TileEntityInfoPanel) getTileEntity(inventory, data));
	}

	public ContainerHoloPanel(int windowId, PlayerInventory inventory, TileEntityInfoPanel panel) {
		super(panel, ModContainerTypes.holo_panel.get(), windowId, ModItems.holo_panel.get(), IWorldPosCallable.of(panel.getWorld(), panel.getPos()));
		addSlot(new SlotCard(panel, 0, 8, 24 + 18) {
			@SuppressWarnings("resource")
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerHoloPanel.this.detectAndSendChanges();
			};
		});
		addSlot(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlot(new SlotPower(panel, 2, 8, 24 + 18 * 3));
		// inventory
		addPlayerInventorySlots(inventory, 201);
	}
}
