package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.item.Item;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(new Item.Properties().group(EnergyControl.ITEM_GROUP).maxStackSize(1));
	}

	/*@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!player.isSneaking() && !world.isRemote && stack.getCount() == 1)
			player.openGui(EnergyControl.instance, BlockDamages.GUI_CARD_HOLDER, world, 0, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}*/
}
