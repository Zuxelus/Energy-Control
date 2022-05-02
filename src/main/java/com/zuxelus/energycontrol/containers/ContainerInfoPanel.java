package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.*;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		addSlotToContainer(new SlotCard(panel, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlotToContainer(new SlotColor(panel, 2, 8, 24 + 18 * 3) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotTouch(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(player, 201);
	}
}
