package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitAppEng extends ItemKitBase {
	public ItemKitAppEng() {
		super(ItemHelper.KIT_APPENG, "kitAppEng");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemKitAppEng";
	}

	@Override
	protected ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		return null;
		//return CrossModLoader.crossAppEng.getSensorCard(stack, world, pos);
	}
}
