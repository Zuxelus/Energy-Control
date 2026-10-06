package com.zuxelus.energycontrol.recipes;

import java.util.Collections;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.zuxelus.energycontrol.tileentities.TileEntityKitAssembler;

public class KitAssemblerRecipeType implements RecipeType<KitAssemblerRecipe> {
	public static final KitAssemblerRecipeType TYPE = new KitAssemblerRecipeType();
	private List<KitAssemblerRecipe> cachedRecipes = Collections.emptyList();

	// same as the types vanilla registers through RecipeType.register
	@Override
	public String toString() {
		return "energycontrol:kit_assembler";
	}

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
		// since 1.21.2 only the server knows the recipes
		if (!(level instanceof ServerLevel serverLevel))
			return;
		RecipeManager recipeManager = serverLevel.recipeAccess();
		cachedRecipes = recipeManager.getRecipes().stream()
				.map(RecipeHolder::value)
				.filter(recipe -> recipe.getType() == this)
				.map(recipe -> (KitAssemblerRecipe) recipe)
				.toList();
	}
}
