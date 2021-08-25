package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(new Item.Properties().group(EnergyControl.ITEM_GROUP).maxStackSize(1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!player.isSneaking() && !world.isRemote && stack.getCount() == 1)
			NetworkHooks.openGui((ServerPlayerEntity) player, new InventoryCardHolder(stack), BlockPos.ZERO);
		return ActionResult.resultSuccess(stack);
	}
}
