package com.zuxelus.energycontrol.containers;

import com.zuxelus.energycontrol.containers.slots.SlotCard;
import com.zuxelus.energycontrol.containers.slots.SlotRange;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.InventoryPortablePanel;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardReader;
import com.zuxelus.zlib.containers.ContainerBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class ContainerPortablePanel extends ContainerBase<InventoryPortablePanel> {
	private EntityPlayer player;
	
	public ContainerPortablePanel(EntityPlayer player) {
		super(new InventoryPortablePanel(player.getHeldItemMainhand(), "item.portable_panel.name"));
		this.player = player;

		addSlotToContainer(new SlotCard(te, 0, 174, 17));
		addSlotToContainer(new SlotRange(te, 1, 174, 35));

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
		if (ItemCardMain.isCard(card)) {
			ItemCardReader reader = new ItemCardReader(card);
			ItemCardMain.updateCardNBT(card, player.worldObj, player.getPosition(), reader, te.getStackInSlot(InventoryPortablePanel.SLOT_UPGRADE_RANGE));
		}
	}
}
