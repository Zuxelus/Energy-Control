package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		// card
		addSlotToContainer(new SlotFilter(panel, 0, 8, 24 + 18));
		// range upgrade
		addSlotToContainer(new SlotFilter(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlotToContainer(new SlotFilter(panel, 2, 8, 24 + 18 * 3));
		// inventory
		addPlayerInventorySlots(player, 190);
	}
}
