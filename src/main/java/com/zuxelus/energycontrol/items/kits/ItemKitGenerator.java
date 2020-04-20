package com.zuxelus.energycontrol.items.kits;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemKitGenerator extends ItemKitSimple {
	public ItemKitGenerator() {
		super(ItemHelper.KIT_GENERATOR, "kitGenerator");
	}

	@Override
	public String getUnlocalizedName() {
		return "item.ItemKitGenerator";
	}

	@Override
	protected BlockPos getTargetCoordinates(World world, BlockPos pos, ItemStack stack) {
		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity != null && (tileentity instanceof TileEntityBaseGenerator || tileentity instanceof TileEntityGeoGenerator))
			return pos;		
		return null;
	}

	@Override
	protected ItemStack getItemCard() {
		return new ItemStack(ItemHelper.itemCard, 1, ItemCardType.CARD_GENERATOR);
	}
}
