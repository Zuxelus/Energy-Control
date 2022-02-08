package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.ItemComponent;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipesNew {

	public static void addRecipes() {
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				GameRegistry.addShapelessRecipe(new ResourceLocation(EnergyControl.MODID + ":card_circuit" + i), null,
						new ItemStack(ModItems.itemComponent, 1, ItemComponent.BASIC_CIRCUIT),
						Ingredient.fromStacks(new ItemStack(ModItems.itemCard, 1, i)));
	}
}
