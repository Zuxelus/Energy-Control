package com.zuxelus.energycontrol.crossmod.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;

public class KitAssemblerRecipeHandler implements IRecipeHandler<KitAssemblerRecipeWrapper> { // 1.10.2

	@Override
	public Class<KitAssemblerRecipeWrapper> getRecipeClass() {
		return KitAssemblerRecipeWrapper.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return KitAssemblerRecipeCategory.id;
	}

	@Override
	public String getRecipeCategoryUid(KitAssemblerRecipeWrapper recipe) {
		return KitAssemblerRecipeCategory.id;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(KitAssemblerRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(KitAssemblerRecipeWrapper recipe) {
		if (recipe.getInputs().isEmpty()) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Recipe has no inputs. {}", new Object[] { recipeInfo });
		}
		if (!recipe.getOutputs().isEmpty()) {
			String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, this);
			Log.error("Kit Assembler Recipe should not have outputs. {}", new Object[] { recipeInfo });
		}
		return true;
	}
}