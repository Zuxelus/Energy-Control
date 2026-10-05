package com.zuxelus.energycontrol.recipes;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public class KitAssemblerRecipeType implements RecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	public List<KitAssemblerRecipe> getRecipes(World level) {
		if (level == null)
			return Collections.emptyList();

		if (cachedRecipes.isEmpty())
			loadRecipes(level);
		return cachedRecipes;
	}

	public KitAssemblerRecipe findRecipe(TileEntityKitAssembler te) {
		if (cachedRecipes.isEmpty())
			loadRecipes(te.getWorld());
		for(KitAssemblerRecipe recipe : cachedRecipes) {
			if (recipe.isSuitable(te))
				return recipe; 
		}
		return null;
	}

	private void loadRecipes(World level) {
		if (level == null)
			return;
		RecipeManager recipeManager = level.getRecipeManager();
		List<KitAssemblerRecipe> recipes = recipeManager.listAllOfType(this);
		cachedRecipes = recipes;
	}
}
