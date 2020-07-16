package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.items.ItemHelper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class NanoBowRecipeTR extends ShapedRecipes {

	public NanoBowRecipeTR() {
		super("", 3, 3, NonNullList.withSize(9, Ingredient.EMPTY), new ItemStack(ItemHelper.itemNanoBow));
		recipeItems.set(1, Ingredient.fromStacks(CrossModLoader.techReborn.getItemStack("carbon_plate")));
		recipeItems.set(2, Ingredient.fromStacks(CrossModLoader.techReborn.getItemStack("glassfiber")));
		ItemStack stack = new ItemStack(CrossModLoader.techReborn.getItemStack("energy_crystal").getItem(), 1, Short.MAX_VALUE);
		recipeItems.set(3, Ingredient.fromStacks(stack));
		recipeItems.set(4, Ingredient.EMPTY);
		recipeItems.set(5, Ingredient.fromStacks(CrossModLoader.techReborn.getItemStack("glassfiber")));
		recipeItems.set(6, Ingredient.EMPTY);
		recipeItems.set(7, Ingredient.fromStacks(CrossModLoader.techReborn.getItemStack("carbon_plate")));
		recipeItems.set(8, Ingredient.fromStacks(CrossModLoader.techReborn.getItemStack("glassfiber")));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = inv.getStackInSlot(3);
		if (stack.isEmpty() || stack.getItem() != CrossModLoader.techReborn.getItemStack("energy_crystal").getItem())
			return ItemStack.EMPTY;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("energy"))
			return new ItemStack(ItemHelper.itemNanoBow);
		double energy = tag.getDouble("energy");
		return ItemStackHelper.getStackWithEnergy(ItemHelper.itemNanoBow, Math.min(energy, 40000.0D));
	}
}
