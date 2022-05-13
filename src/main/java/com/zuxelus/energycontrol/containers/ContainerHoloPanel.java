package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotPower;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerHoloPanel extends ContainerBase<TileEntityInfoPanel> {
	private EntityPlayer player;

	public ContainerHoloPanel(EntityPlayer player, TileEntityInfoPanel te) {
		super(te);
		this.player = player;
		addSlotToContainer(new SlotCard(te, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (te.getWorldObj().isRemote)
					ContainerHoloPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotRange(te, 1, 8, 24 + 18 * 2));
		addSlotToContainer(new SlotPower(te, 2, 8, 24 + 18 * 3));
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
