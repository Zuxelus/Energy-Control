package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.*;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	private EntityPlayer player;

	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		this.player = player;
		addSlotToContainer(new SlotCard(panel, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlotToContainer(new SlotColor(panel, 2, 8, 24 + 18 * 3) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotTouch(panel, 3, 8, 24 + 18 * 4));
		// inventory
		addPlayerInventorySlots(player, 201);
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
