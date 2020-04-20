package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitCounter extends ItemKitSimple {
	public ItemKitCounter() {
		super(ItemHelper.KIT_COUNTER, "kitCounter");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemKitCounter";
	}

	@Override
	protected BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack) {
		TileEntity entity = world.getTileEntity(pos);
		if (entity != null && (entity instanceof TileEntityEnergyCounter || entity instanceof TileEntityAverageCounter))
			return pos;
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_COUNTER);
	}
}
