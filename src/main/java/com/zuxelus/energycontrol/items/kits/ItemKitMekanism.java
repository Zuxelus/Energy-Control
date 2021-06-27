package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import mekanism.generators.common.tile.reactor.TileEntityReactorController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitMekanism extends ItemKitBase {

	public ItemKitMekanism() {
		super(ItemCardType.KIT_MEKANISM, "kit_mekanism");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileEntityReactorController) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_MEKANISM);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return ItemStack.EMPTY;
	}
}
