package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemPortablePanel extends Item {

	public ItemPortablePanel() {
		super(new Item.Properties().stacksTo(1).tab(EnergyControl.ITEM_GROUP));
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && !world.isClientSide && stack.getCount() == 1)
			NetworkHooks.openGui((ServerPlayerEntity) player, new InventoryPortablePanel(stack), BlockPos.ZERO);
		return ActionResult.success(stack);
	}
}
