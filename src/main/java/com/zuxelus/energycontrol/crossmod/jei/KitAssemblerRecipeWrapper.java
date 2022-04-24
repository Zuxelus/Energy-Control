package com.zuxelus.energycontrol.crossmod.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class KitAssemblerRecipeWrapper extends BlankRecipeWrapper {
	private final List<List<ItemStack>> inputs;
	private final ItemStack output;

	public KitAssemblerRecipeWrapper(KitAssemblerRecipe recipe) {
		inputs = new ArrayList<List<ItemStack>>();
		inputs.add(Collections.singletonList(recipe.input1));
		inputs.add(Collections.singletonList(recipe.input2));
		inputs.add(Collections.singletonList(recipe.input3));
		output = recipe.output;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	@Override
	public List getInputs() {
		return inputs;
	}

	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(output);
	}
}
