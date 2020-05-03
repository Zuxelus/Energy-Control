package com.zuxelus.energycontroladdon;

import com.zuxelus.energycontrol.api.ItemStackHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KitFurnace extends KitBase {
	public static int KIT_ID = 50;

	public KitFurnace() {
		super("kit_furnace", KIT_ID);
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, Item card, EntityPlayer player, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityFurnace))
			return ItemStack.EMPTY;

		ItemStack sensorLocationCard = new ItemStack(card, 1, CardFurnace.CARD_ID);
		ItemStackHelper.setCoordinates(sensorLocationCard, pos);
		return sensorLocationCard;
	}
}
