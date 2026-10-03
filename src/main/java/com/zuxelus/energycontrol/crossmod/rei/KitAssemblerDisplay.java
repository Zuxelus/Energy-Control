package com.zuxelus.energycontrol.crossmod.rei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.recipes.KitAssemblerRecipe;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;

public class KitAssemblerDisplay extends BasicDisplay implements SimpleGridMenuDisplay {
	public final int count1;
	public final int count2;
	public final int count3;
	private int time;

	public KitAssemblerDisplay(KitAssemblerRecipe recipe) {
		super(List.of(withCount(recipe.input1, recipe.count1), withCount(recipe.input2, recipe.count2), withCount(recipe.input3, recipe.count3)),
				Collections.singletonList(EntryIngredients.of(recipe.output)));
		this.count1 = recipe.count1;
		this.count2 = recipe.count2;
		this.count3 = recipe.count3;
		time = recipe.time;
	}

	public KitAssemblerDisplay(List<EntryIngredient> input, List<EntryIngredient> output, NbtCompound tag) {
		super(input, output);
		count1 = tag.getInt("count1");
		count2 = tag.getInt("count2");
		count3 = tag.getInt("count3");
		time = tag.getInt("time");
	}

	// shows the required amount on each ingredient (the recipe keeps counts separately from its ingredients)
	private static EntryIngredient withCount(Ingredient ingredient, int count) {
		List<ItemStack> stacks = new ArrayList<>();
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			ItemStack copy = stack.copy();
			copy.setCount(count);
			stacks.add(copy);
		}
		return EntryIngredients.ofItemStacks(stacks);
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return KitAssemblerRecipeCategory.id;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 3;
	}

	public int getTime() {
		return this.time;
	}

	public static <R extends KitAssemblerDisplay> BasicDisplay.Serializer<R> serializer(BasicDisplay.Serializer.RecipeLessConstructor<R> constructor) {
		return BasicDisplay.Serializer.ofRecipeLess(constructor, (display, tag) -> {
			tag.putInt("count1", display.count1);
			tag.putInt("count2", display.count2);
			tag.putInt("count3", display.count3);
			tag.putInt("time", display.getTime());
		});
	}
}