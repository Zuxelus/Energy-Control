package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.tileentities.TileEntityAdvancedInfoPanel;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ContainerAdvancedInfoPanel extends ContainerBase<TileEntityAdvancedInfoPanel> {
	private EntityPlayer player;

	public ContainerAdvancedInfoPanel(EntityPlayer player, TileEntityAdvancedInfoPanel panel) {
		super(panel);
		this.player = player;

		// cards
		addSlotToContainer(new SlotCard(panel, 0, 8, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
				ContainerAdvancedInfoPanel.this.onCraftMatrixChanged(panel);
			}
		});
		addSlotToContainer(new SlotCard(panel, 1, 8 + 18, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
				ContainerAdvancedInfoPanel.this.onCraftMatrixChanged(panel);
			}
		});
		addSlotToContainer(new SlotCard(panel, 2, 8 + 36, 24 + 18) {
			@Override
			public void onSlotChanged() {
				if (panel.getWorldObj().isRemote)
				ContainerAdvancedInfoPanel.this.onCraftMatrixChanged(panel);
			}
		});
		addSlotToContainer(new SlotRange(panel, 3, 8 + 54, 24 + 18));
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
