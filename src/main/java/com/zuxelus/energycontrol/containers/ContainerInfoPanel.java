package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotColor;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.containers.slots.SlotTouch;
import com.zuxelus.energycontrol.tileentities.TileEntityInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerInfoPanel extends ContainerBase<TileEntityInfoPanel> {
	private EntityPlayer player;

	public ContainerInfoPanel(EntityPlayer player, TileEntityInfoPanel panel) {
		super(panel);
		this.player = player;
		// card
		addSlotToContainer(new SlotCard(panel, 0, 8, 24 + 18));
		// range upgrade
		addSlotToContainer(new SlotRange(panel, 1, 8, 24 + 18 * 2));
		// color upgrade
		addSlotToContainer(new SlotColor(panel, 2, 8, 24 + 18 * 3));
		// touch upgrade
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
