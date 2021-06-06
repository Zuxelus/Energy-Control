package com.zuxelus.energycontrol.recipes;

import com.zuxelus.energycontrol.EnergyControl;
import com.zuxelus.energycontrol.crossmod.CrossModLoader;
import com.zuxelus.energycontrol.crossmod.ModIDs;
import com.zuxelus.energycontrol.init.ModItems;
import com.zuxelus.energycontrol.items.cards.ItemCardMain;
import com.zuxelus.energycontrol.items.cards.ItemCardType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipesNew {

	public static void addRecipes() {
		ItemStack stack = CrossModLoader.getCrossMod(ModIDs.IC2).getItemStack("circuit");
		if (stack.isEmpty())
			stack = CrossModLoader.getCrossMod(ModIDs.TECH_REBORN).getItemStack("circuit");
		if (stack.isEmpty())
			return;
		for (int i = 0; i <= ItemCardType.CARD_MAX; i++)
			if (ItemCardMain.containsCard(i))
				GameRegistry.addShapelessRecipe(new ResourceLocation(EnergyControl.MODID + ":card_circuit" + i), null,
						stack.copy(),
						Ingredient.fromStacks(new ItemStack(ModItems.itemCard, 1, i)));
	}
}
