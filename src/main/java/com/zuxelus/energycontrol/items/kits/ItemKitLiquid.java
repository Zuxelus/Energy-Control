package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.LiquidCardHelper;
import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ItemKitLiquid extends ItemKitSimple {
	public ItemKitLiquid() {
		super(ItemHelper.KIT_LIQUID, "kitLiquid");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemKitLiquid";
	}

	@Override
	protected BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack) {
		IFluidTankProperties tank = LiquidCardHelper.getStorageAt(world, pos);
		if (tank != null)
			return pos;
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_LIQUID);
	}
}
