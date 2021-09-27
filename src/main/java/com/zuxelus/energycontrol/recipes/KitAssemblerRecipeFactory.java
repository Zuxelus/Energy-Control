package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class KitAssemblerRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		Ingredient input1 = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "input1"), context);
		int count1 = JsonUtils.getInt(JsonUtils.getJsonObject(json, "input1"), "count", 1);
		Ingredient input2 = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "input2"), context);
		int count2 = JsonUtils.getInt(JsonUtils.getJsonObject(json, "input2"), "count", 1);
		Ingredient input3 = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "input3"), context);
		int count3 = JsonUtils.getInt(JsonUtils.getJsonObject(json, "input3"), "count", 1);
		ItemStack output = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
		int time = JsonUtils.getInt(json, "time", 300);
		return new KitAssemblerRecipe(input1, count1, input2, count2, input3, count3, output, time);
	}
}
