package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityAdvancedInfoPanel> {
	private EntityPlayer player;

	public ContainerAdvancedInfoPanel(EntityPlayer player, TileEntityAdvancedInfoPanel te) {
		super(te);
		this.player = player;

		// cards
		addSlotToContainer(new SlotCard(te, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (te.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotCard(te, 1, 8 + 18, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (te.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotCard(te, 2, 8 + 36, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (te.getWorld().isRemote)
					ContainerAdvancedInfoPanel.this.detectAndSendChanges();
			}
		});
		addSlotToContainer(new SlotRange(te, 3, 8 + 54, 24 + 18));
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
