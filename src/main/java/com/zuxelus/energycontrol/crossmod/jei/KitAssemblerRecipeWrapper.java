package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.List;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class KitAssemblerRecipeWrapper implements IRecipeWrapper {
	public final KitAssemblerRecipe recipe;

	public KitAssemblerRecipeWrapper(KitAssemblerRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		List<ItemStack> items = new ArrayList<>();
		items.addAll(getInputList(1));
		items.addAll(getInputList(2));
		items.addAll(getInputList(3));
		ingredients.setInputs(VanillaTypes.ITEM, items);
		ingredients.setOutput(VanillaTypes.ITEM, recipe.output);
	}

	public List<ItemStack> getInputList(int slot) {
		if (slot == 2)
			return getStackList(recipe.input2, recipe.count2);
		if (slot == 3)
			return getStackList(recipe.input3, recipe.count3);
		return getStackList(recipe.input1, recipe.count1);
	}

	public ItemStack getOutput() {
		return recipe.output;
	}

	private static List<ItemStack> getStackList(Ingredient ingredient, int count) {
		List<ItemStack> list = new ArrayList<>();
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			if (count == 1)
				list.add(stack);
			else {
				ItemStack copy = stack.copy();
				copy.setCount(count);
				list.add(copy);
			}
		}
		return list;
	}
}
