package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKitHBM extends ItemKitBase {

	public ItemKitHBM() {
		super(ItemCardType.KIT_HBM, "kit_hbm");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			te = getTe(world, x, y, z);

		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.HBM).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_HBM);
			ItemStackHelper.setCoordinates(newCard, te.xCoord, te.yCoord, te.zCoord);
			return newCard;
		}
		return null;
	}

	private TileEntity getTe(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block != null) {
			int offsetX = 0;
			int offsetY = 0;
			if (block.getClass().getName().equals("com.hbm.blocks.machine.ReactorHatch"))
				offsetX = 2;
			else if (block.getClass().getName().equals("com.hbm.blocks.machine.WatzHatch"))
				offsetX = 3;
			else if (block.getClass().getName().equals("com.hbm.blocks.machine.FWatzHatch")) {
				offsetX = 9;
				offsetY = 11;
			} else
				return null;
			int meta = world.getBlockMetadata(x, y, z);
			if (meta == 2)
				return world.getTileEntity(x, y + offsetY, z + offsetX);
			if (meta == 3)
				return world.getTileEntity(x, y + offsetY, z - offsetX);
			if (meta == 4)
				return world.getTileEntity(x + offsetX, y + offsetY, z);
			if (meta == 5)
				return world.getTileEntity(x - offsetX, y + offsetY, z);
		}
		return null;
	}
}
