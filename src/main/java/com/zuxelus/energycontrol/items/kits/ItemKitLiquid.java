package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;

public class ItemKitLiquid extends ItemKitSimple {
	public ItemKitLiquid() {
		super(ItemCardType.KIT_LIQUID, "kit_liquid");
	}

	@Override
	protected BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack) {
		IFluidTank tank = LiquidCardHelper.getStorageAt(world, pos);
		if (tank != null)
			return pos;
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_LIQUID);
	}
}
