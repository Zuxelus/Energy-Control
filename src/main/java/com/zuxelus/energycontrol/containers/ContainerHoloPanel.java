package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.*;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerHoloPanel extends ContainerBase<TileEntityInfoPanel> {
	private EntityPlayer player;

	public ContainerHoloPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		this.player = player;
		addSlotToContainer(new SlotCard(panel, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorld().isRemote)
					ContainerHoloPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		addSlotToContainer(new SlotPower(panel, 2, 8, 24 + 18 * 3));
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
