package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitBigReactor extends ItemKitBase {

	public ItemKitBigReactor() {
		super(ItemHelper.KIT_BIGREACTOR, "kit_big_reactor");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.kit_big_reactor";
	}

	@Override
	protected ItemStack getSensorCard(ItemStack stack, EntityPlayer player, World world, BlockPos pos) {
		return null;
		//return CrossModLoader.crossBigReactors.getSensorCard(stack, world, pos);
	}
}
