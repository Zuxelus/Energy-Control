package com.zuxelus.energycontrol.items.cards;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.items.InventoryCardHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class ItemCardHolder extends Item {

	public ItemCardHolder() {
		super(new Item.Properties().tab(EnergyControl.ITEM_GROUP).stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!player.isShiftKeyDown() && !level.isClientSide && stack.getCount() == 1)
			NetworkHooks.openGui((ServerPlayer) player, new InventoryCardHolder(stack), BlockPos.ZERO);
		return InteractionResultHolder.success(stack);
	}
}
