package com.zuxelus.energycontrol.recipes;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class KitAssemblerRecipeType implements IRecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	public List<KitAssemblerRecipe> getRecipes(World world) {
		if (world == null)
			return Collections.emptyList();

		if (cachedRecipes.isEmpty()) {
			RecipeManager recipeManager = world.getRecipeManager();
			List<KitAssemblerRecipe> recipes = getAllRecipesFor(recipeManager);
			cachedRecipes = recipes;
		}
		return cachedRecipes;
	}

	private List<KitAssemblerRecipe> getAllRecipesFor(RecipeManager manger) {
		// Exists in 1.16.5
		try {
			Field field = RecipeManager.class.getDeclaredField("recipes");
			field.setAccessible(true);
			return ((Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>>) field.get(manger)).getOrDefault(this, Collections.emptyMap()).values().stream().map((recipe) -> {
				return (KitAssemblerRecipe) recipe;
			}).collect(Collectors.toList());
		} catch (Throwable ignored) {
		}
		return Collections.emptyList();
	}

	public KitAssemblerRecipe findRecipe(TileEntityKitAssembler te) {
		for(KitAssemblerRecipe recipe : cachedRecipes) {
			if (recipe.isSuitable(te))
				return recipe; 
		}
		return null;
	}
}
