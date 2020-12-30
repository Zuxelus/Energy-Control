package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityAdvancedInfoPanel> {
	private EntityPlayer player;

	public ContainerAdvancedInfoPanel(EntityPlayer player, TileEntityAdvancedInfoPanel panel) {
		super(panel);
		this.player = player;

		// cards
		addSlotToContainer(new SlotFilter(panel, 0, 8, 24 + 18));
		addSlotToContainer(new SlotFilter(panel, 1, 8 + 18, 24 + 18));
		addSlotToContainer(new SlotFilter(panel, 2, 8 + 36, 24 + 18));
		// range upgrade
		addSlotToContainer(new SlotFilter(panel, 3, 8 + 54, 24 + 18));
		// inventory
		addPlayerInventorySlots(player, 223);
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
