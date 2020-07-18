package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.api.ItemStackHelper;
import com.zuxelus.energycontrol.items.ItemHelper;

import ic2.api.item.IC2Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class NanoBowRecipe extends ShapedRecipes {

	public NanoBowRecipe() {
		super("", 3, 3, NonNullList.withSize(9, Ingredient.EMPTY), new ItemStack(ItemHelper.itemNanoBow));
		recipeItems.set(1, Ingredient.fromStacks(IC2Items.getItem("crafting", "carbon_plate")));
		recipeItems.set(2, Ingredient.fromStacks(IC2Items.getItem("cable", "type:glass,insulation:0")));
		ItemStack stack = new ItemStack(IC2Items.getItem("energy_crystal").getItem(), 1, Short.MAX_VALUE);
		recipeItems.set(3, Ingredient.fromStacks(stack));
		recipeItems.set(4, Ingredient.EMPTY);
		recipeItems.set(5, Ingredient.fromStacks(IC2Items.getItem("cable", "type:glass,insulation:0")));
		recipeItems.set(6, Ingredient.EMPTY);
		recipeItems.set(7, Ingredient.fromStacks(IC2Items.getItem("crafting", "carbon_plate")));
		recipeItems.set(8, Ingredient.fromStacks(IC2Items.getItem("cable", "type:glass,insulation:0")));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stack = inv.getStackInSlot(3);
		if (stack.isEmpty() || stack.getItem() != IC2Items.getItem("energy_crystal").getItem())
			return ItemStack.EMPTY;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null || !tag.hasKey("charge"))
			return new ItemStack(ItemHelper.itemNanoBow);
		double energy = tag.getDouble("charge");
		return ItemStackHelper.getStackWithEnergy(ItemHelper.itemNanoBow, "charge", Math.min(energy, 40000.0D));
	}
}
