package com.zuxelus.energycontrol;

import com.zuxelus.energycontrol.items.ItemHelper;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import com.zuxelus.energycontrol.items.kits.ItemKitMain;

import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;

public class RecipesNew {

	public static void addRecipes() {
		ItemKitMain.registerRecipes();
		ItemCardMain.registerRecipes();
		
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				Recipes.advRecipes.addShapelessRecipe(IC2Items.getItem("crafting","circuit"), new ItemStack(ItemHelper.itemCard, 1, i));
	}
}
