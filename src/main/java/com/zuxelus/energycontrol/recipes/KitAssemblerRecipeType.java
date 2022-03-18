package com.zuxelus.energycontrol.recipes;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.world.World;

public class KitAssemblerRecipeType implements IRecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	public List<KitAssemblerRecipe> getRecipes(World world) {
		if (world == null)
			return Collections.emptyList();

		if (cachedRecipes.isEmpty())
			loadRecipes(world);
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

	private void loadRecipes(World world) {
		if (world == null)
			return;
		RecipeManager recipeManager = world.getRecipeManager();
		List<KitAssemblerRecipe> recipes = recipeManager.getAllRecipesFor(this);
		cachedRecipes = recipes;
	}
}
