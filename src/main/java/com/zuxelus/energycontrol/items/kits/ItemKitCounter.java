package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemKitCounter extends ItemKitSimple {
	public ItemKitCounter() {
		super(ItemCardType.KIT_COUNTER, "kit_counter");
	}

	@Override
	protected ChunkCoordinates getTargetCoordinates(World world, int x, int y, int z, ItemStack stack) {
		TileEntity entity = world.getTileEntity(x, y, z);
		if (entity != null && (entity instanceof TileEntityEnergyCounter || entity instanceof TileEntityAverageCounter))
			return new ChunkCoordinates(x, y, z);
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_COUNTER);
	}
}
