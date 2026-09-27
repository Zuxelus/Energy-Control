package com.zuxelus.zlib.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public final class EmptyInventory implements RecipeInput {
	public static final EmptyInventory INSTANCE = new EmptyInventory();

	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 0;
	}
}
