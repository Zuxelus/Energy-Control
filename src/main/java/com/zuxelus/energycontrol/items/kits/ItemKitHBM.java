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
import net.minecraft.world.World;

public class ItemKitHBM extends ItemKitBase {

	public ItemKitHBM() {
		super(ItemCardType.KIT_HBM, "kit_hbm");
	}

	@Override
	public ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			/*IBlockState state = world.getBlockState(pos);
			if (state.getBlock() != null && state.getBlock().getClass().getName().equals("com.hbm.blocks.machine.FactoryHatch"))
				for (EnumFacing dir : EnumFacing.HORIZONTALS) {
					te = world.getTileEntity(pos.offset(dir));
					if (te != null)
						break;
				}*/
		}
		NBTTagCompound tag = CrossModLoader.getCrossMod(ModIDs.HBM).getCardData(te);
		if (tag != null) {
			ItemStack newCard = new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_HBM);
			ItemStackHelper.setCoordinates(newCard, x, y, z);
			return newCard;
		}
		return null;
	}
}
