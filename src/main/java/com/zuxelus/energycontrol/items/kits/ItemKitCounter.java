package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.tileentities.TileEntityAverageCounter;
import com.zuxelus.energycontrol.tileentities.TileEntityEnergyCounter;

import ic2.api.item.IC2Items;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitCounter extends ItemKitSimple {
	public ItemKitCounter() {
		super(ItemCardType.KIT_COUNTER, "kit_counter");
		//addRecipe(new Object[] { "CF", "PR", 'P', Items.PAPER, 'C', "circuitBasic", 'F', IC2Items.getItem("frequency_transmitter"), 'R', "dyeOrange" });
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
