package com.zuxelus.energycontrol.items;

import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class ItemPortablePanel extends Item {

	public ItemPortablePanel() {
		super(new Item.Properties().stacksTo(1).tab(EnergyControl.ITEM_GROUP));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && !level.isClientSide && stack.getCount() == 1)
			NetworkHooks.openScreen((ServerPlayer) player, new InventoryPortablePanel(stack), BlockPos.ZERO);
		return InteractionResultHolder.success(stack);
	}
}
