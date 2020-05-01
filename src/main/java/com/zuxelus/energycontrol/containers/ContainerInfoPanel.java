package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	private EntityPlayer player;

	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		this.player = player;
		// card
		addSlotToContainer(new SlotFilter(panel, 0, 8, 24 + 18));
		// range upgrade
		addSlotToContainer(new SlotFilter(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlotToContainer(new SlotFilter(panel, 2, 8, 24 + 18 * 3));
		// inventory
		addPlayerInventorySlots(player, 190);
	}

	@Override
	public void detectAndSendChanges() {
		if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).isChangingQuantityOnly) {
			((EntityPlayerMP) player).isChangingQuantityOnly = false;
			super.detectAndSendChanges();
			((EntityPlayerMP) player).isChangingQuantityOnly = true;
		} else
			super.detectAndSendChanges();
	}
}
