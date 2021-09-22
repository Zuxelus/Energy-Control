package com.zuxelus.energycontrol.recipes;

import java.util.Collections;
import java.util.List;

import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;
import com.zuxelus.zlib.recipes.EmptyInventory;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class KitAssemblerRecipeType implements RecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	public List<KitAssemblerRecipe> getRecipes(Level world) {
		if (world == null)
			return Collections.emptyList();

		if (cachedRecipes.isEmpty()) {
			RecipeManager recipeManager = world.getRecipeManager();
			List<KitAssemblerRecipe> recipes = recipeManager.getRecipesFor(this, EmptyInventory.INSTANCE, world);
			cachedRecipes = recipes;
		}
		return cachedRecipes;
	}

	public KitAssemblerRecipe findRecipe(TileEntityKitAssembler te) {
		for(KitAssemblerRecipe recipe : cachedRecipes) {
			if (recipe.isSuitable(te))
				return recipe; 
		}
		return null;
	}
}
