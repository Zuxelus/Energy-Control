package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ArrayRecipeSerializer implements RecipeSerializer<StorageArrayRecipe> {

	@Override
	public StorageArrayRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		return new StorageArrayRecipe((ShapelessRecipe) RecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
	}

	@Override
	public StorageArrayRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		try {
			return new StorageArrayRecipe((ShapelessRecipe) RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer));
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error reading storage array recipe from packet.", e);
			throw e;
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, StorageArrayRecipe recipe) {
		try {
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe.getRecipe());
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error writing storage array recipe to packet.", e);
			throw e;
		}
	}
}
