package com.zuxelus.energycontrol.containers;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotArmor extends Slot {
	private final EntityEquipmentSlot armorType;

	public SlotArmor(InventoryPlayer inventory, EntityEquipmentSlot armorType, int x, int y) {
		super(inventory, 36 + armorType.getIndex(), x, y);
		this.armorType = armorType;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack.isEmpty())
			return false;
		Item item = stack.getItem();
		if (item == null)
			return false;
		return item.isValidArmor(stack, armorType, ((InventoryPlayer) inventory).player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()];
	}
}
