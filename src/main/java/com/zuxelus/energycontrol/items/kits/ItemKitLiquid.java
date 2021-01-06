package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;

public class ItemKitLiquid extends ItemKitSimple {
	public ItemKitLiquid() {
		super(ItemCardType.KIT_LIQUID, "kit_liquid");
	}

	@Override
	protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack) {
		FluidTankInfo tank = CrossModLoader.getTankAt(world, x, y, z);
		if (tank != null)
			return new ChunkCoordinates(x, y, z);
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ModItems.itemCard, 1, ItemCardType.CARD_LIQUID);
	}
}
