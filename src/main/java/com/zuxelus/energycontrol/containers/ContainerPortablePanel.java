package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.ContainerBase;
import com.zuxelus.zlib.containers.slots.SlotFilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private EntityPlayer player;
	
	public ContainerPortablePanel(EntityPlayer player) {
		super(new InventoryPortablePanel(player.getHeldItem(), "item.portable_panel.name"));
		this.player = player;

		addSlotToContainer(new SlotFilter(te, 0, 174, 17));
		addSlotToContainer(new SlotFilter(te, 1, 174, 35));

		addPlayerInventoryTopSlots(player, 8, 188, ModItems.itemPortablePanel);
	}

	@Override
	public void detectAndSendChanges() {
		processCard();
		if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).isChangingQuantityOnly) {
			((EntityPlayerMP) player).isChangingQuantityOnly = false;
			super.detectAndSendChanges();
			((EntityPlayerMP) player).isChangingQuantityOnly = true;
		} else
			super.detectAndSendChanges();
	}

	private void processCard() {
		ItemStack card = te.getStackInSlot(InventoryPortablePanel.SLOT_CARD);
		if (card == null)
			return;

		Item item = card.getItem();
		if (!(item instanceof ItemCardMain))
			return;

		ItemCardReader reader = new ItemCardReader(card);
		ItemCardMain.updateCardNBT(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ, reader, te.getStackInSlot(InventoryPortablePanel.SLOT_UPGRADE_RANGE));
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		te.writeToParentNBT(player);
	}
}
