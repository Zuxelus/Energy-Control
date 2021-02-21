package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;

import appeng.api.util.AEPartLocation;
import appeng.me.helpers.IGridProxyable;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.StorageMonitorPart;
import appeng.tile.networking.CableBusTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitAppEng extends ItemKitMain {

	@Override
	public ItemStack getSensorCard(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CableBusTileEntity) {
			CableBusContainer cb = ((CableBusTileEntity) te).getCableBus();
			if (cb != null && cb.getPart(AEPartLocation.fromFacing(side)) instanceof StorageMonitorPart) {
				ItemStack newCard = new ItemStack(ModItems.card_app_eng_inv.get());
				ItemStackHelper.setCoordinates(newCard, pos);
				return newCard;
			}
		}
		if (te instanceof IGridProxyable) {
			ItemStack newCard = new ItemStack(ModItems.card_app_eng.get());
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
