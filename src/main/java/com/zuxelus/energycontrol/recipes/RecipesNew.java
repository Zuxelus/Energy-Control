package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipesNew {

	public static void addRecipes() {
		ItemStack stack = CrossModLoader.ic2.getItemStack("circuit");
		if (stack == null)
			stack = CrossModLoader.techReborn.getItemStack("circuit");
		if (stack == null)
			return;
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				GameRegistry.addShapelessRecipe(new ResourceLocation(EnergyControl.MODID + ":card_circuit"), null,
						stack,
						Ingredient.fromStacks(new ItemStack(ModItems.itemCard, 1, i)));
	}
}
