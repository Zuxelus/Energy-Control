package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.me.helpers.IGridConnectedBlockEntity;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.StorageMonitorPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemKitAppEng extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, Player player, Level world, BlockPos pos, Direction side) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof CableBusBlockEntity) {
			CableBusContainer cb = ((CableBusBlockEntity) te).getCableBus();
			if (cb != null && cb.getPart(side) instanceof StorageMonitorPart) {
				ItemStack newCard = new ItemStack(ModItems.card_app_eng_inv);
				ItemStackHelper.setCoordinates(newCard, pos);
				return newCard;
			}
		}
		if (te instanceof IGridConnectedBlockEntity) {
			ItemStack newCard = new ItemStack(ModItems.card_app_eng);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
