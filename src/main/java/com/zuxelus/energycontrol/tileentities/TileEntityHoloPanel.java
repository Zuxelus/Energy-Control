package com.zuxelus.energycontrol.tileentities;

import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.ItemUpgrade;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityHoloPanel extends TileEntityInfoPanel {
	private static final byte SLOT_CARD = 0;
	private static final byte SLOT_UPGRADE_RANGE = 1;
	private static final byte SLOT_UPGRADE_POWER = 2;

	public int getPower() {
		ItemStack stack = getStackInSlot(SLOT_UPGRADE_POWER);
		if (stack == null)
			return 1;
		return stack.stackSize + 1;
	}

	@Override
	public boolean isItemValid(int index, ItemStack stack) { // ISlotItemFilter
		switch (index) {
		case SLOT_CARD:
			return ItemCardMain.isCard(stack);
		case SLOT_UPGRADE_RANGE:
			return stack.getItem() instanceof ItemUpgrade && stack.getItemDamage() == ItemUpgrade.DAMAGE_RANGE;
		case SLOT_UPGRADE_POWER:
			return stack.getItem() instanceof ItemComponent && stack.getItemDamage() == ItemComponent.ADVANCED_CIRCUIT;
		default:
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		if (screen == null)
			return AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		return AxisAlignedBB.getBoundingBox(screen.minX, screen.minY, screen.minZ, screen.maxX + 1, screen.maxY + getPower(), screen.maxZ + 1);
	}

	// IWrenchable
	@Override
	public ItemStack getWrenchDrop(EntityPlayer player) {
		return new ItemStack(ModItems.blockHoloPanel);
	}
}
