package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		// card
		addSlotToContainer(new SlotFilter(panel, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			};
		});
		// range upgrade
		addSlotToContainer(new SlotFilter(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlotToContainer(new SlotFilter(panel, 2, 8, 24 + 18 * 3) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			};
		});
		// touch upgrade
		addSlotToContainer(new SlotFilter(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(player, 201);
	}
}
