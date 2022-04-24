package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitNuclearCraft extends ItemKitBase {

	public ItemKitNuclearCraft() {
		super(ItemCardType.KIT_NUCLEARCRAFT, "kit_nuclearcraft");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		TileEntity te = world.getTileEntity(pos);
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.NUCLEAR_CRAFT).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_NUCLEARCRAFT);
			ItemStackHelper.setCoordinates(newCard, pos);
			return newCard;
		}
		return null;
	}
}
