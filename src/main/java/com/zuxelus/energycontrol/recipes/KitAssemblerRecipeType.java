package com.zuxelus.energycontrol.recipes;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KitAssemblerRecipeType implements RecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	public List<KitAssemblerRecipe> getRecipes(Level level) {
		if (level == null)
			return Collections.emptyList();

		if (cachedRecipes.isEmpty())
			loadRecipes(level);
		return cachedRecipes;
	}

	public KitAssemblerRecipe findRecipe(TileEntityKitAssembler te) {
		if (cachedRecipes.isEmpty())
			loadRecipes(te.getLevel());
		for(KitAssemblerRecipe recipe : cachedRecipes) {
			if (recipe.isSuitable(te))
				return recipe; 
		}
		return null;
	}

	private void loadRecipes(Level level) {
		if (level == null)
			return;
		RecipeManager recipeManager = level.getRecipeManager();
		List<KitAssemblerRecipe> recipes = recipeManager.getAllRecipesFor(this);
		cachedRecipes = recipes;
	}
}
