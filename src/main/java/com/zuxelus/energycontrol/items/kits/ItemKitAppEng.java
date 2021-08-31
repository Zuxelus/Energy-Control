package com.zuxelus.energycontrol.items.kits;

import appeng.api.util.AEPartLocation;
import appeng.me.helpers.IGridProxyable;
import appeng.parts.CableBusContainer;
import appeng.parts.reporting.PartStorageMonitor;
import appeng.tile.networking.TileCableBus;
import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitAppEng extends ItemKitBase {

	public ItemKitAppEng() {
		super(ItemCardType.KIT_APPENG, "kit_app_eng");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileCableBus) {
			CableBusContainer cb = ((TileCableBus) te).getCableBus();
			if (cb != null && cb.getPart(AEPartLocation.fromFacing(side)) instanceof PartStorageMonitor) {
				ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_APPENG_INV);
				ItemStackHelper.setCoordinates(newCard, pos);
				return newCard;
			}
		}
		if (te instanceof IGridProxyable) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_APPENG);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
