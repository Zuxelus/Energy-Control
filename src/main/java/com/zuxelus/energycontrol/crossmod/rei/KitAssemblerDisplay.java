package com.zuxelus.energycontrol.crossmod.rei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class KitAssemblerDisplay implements RecipeDisplay {
	public static final Identifier ID = new Identifier(EnergyControl.MODID, "kit_assembler");
	private final Identifier recipeId;
	private final List<List<EntryStack>> input;
	private final List<EntryStack> output;
	public final int count1;
	public final int count2;
	public final int count3;
	private final int time;

	public KitAssemblerDisplay(KitAssemblerRecipe recipe) {
		recipeId = recipe.getId();
		input = Arrays.asList(withCount(recipe.input1, recipe.count1), withCount(recipe.input2, recipe.count2), withCount(recipe.input3, recipe.count3));
		output = Collections.singletonList(EntryStack.create(recipe.output));
		count1 = recipe.count1;
		count2 = recipe.count2;
		count3 = recipe.count3;
		time = recipe.time;
	}

	// shows the required amount on each ingredient (the recipe keeps counts separately from its ingredients)
	private static List<EntryStack> withCount(Ingredient ingredient, int count) {
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : ingredient.getMatchingStacksClient()) {
			ItemStack copy = stack.copy();
			copy.setCount(count);
			stacks.add(copy);
		}
		return EntryStack.ofItemStacks(stacks);
	}

	@Override
	public List<List<EntryStack>> getInputEntries() {
		return input;
	}

	@Override
	public List<EntryStack> getOutputEntries() {
		return output;
	}

	@Override
	public Identifier getRecipeCategory() {
		return ID;
	}

	@Override
	public Optional<Identifier> getRecipeLocation() {
		return Optional.of(recipeId);
	}

	public int getTime() {
		return time;
	}
}
