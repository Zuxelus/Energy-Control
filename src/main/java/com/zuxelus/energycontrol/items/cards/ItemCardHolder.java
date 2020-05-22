package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.blocks.BlockDamages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		setMaxStackSize(1);
		setCreativeTab(EnergyControl.creativeTab);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!player.isSneaking() && !world.isRemote && stack.stackSize == 1)
			player.openGui(EnergyControl.instance, BlockDamages.GUI_CARD_HOLDER, world, 0, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}
}
