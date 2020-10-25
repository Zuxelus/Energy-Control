package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import appeng.me.helpers.IGridProxyable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitAppEng extends ItemKitBase {

	public ItemKitAppEng() {
		super(ItemCardType.KIT_APPENG, "kit_app_eng");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IGridProxyable) {
			ItemStack sensorLocationCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_APPENG);
			ItemStackHelper.setCoordinates(sensorLocationCard, pos);
			return sensorLocationCard;
		}
		return ItemStack.EMPTY;
	}
}
