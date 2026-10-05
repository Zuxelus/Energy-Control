package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;

public class ArrayRecipeSerializer implements RecipeSerializer<StorageArrayRecipe> {

	@Override
	public StorageArrayRecipe read(Identifier recipeId, JsonObject json) {
		return new StorageArrayRecipe((ShapelessRecipe) RecipeSerializer.SHAPELESS.read(recipeId, json));
	}

	@Override
	public StorageArrayRecipe read(Identifier recipeId, PacketByteBuf buffer) {
		try {
			return new StorageArrayRecipe((ShapelessRecipe) RecipeSerializer.SHAPELESS.read(recipeId, buffer));
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error reading storage array recipe from packet.", e);
			throw e;
		}
	}

	@Override
	public void write(PacketByteBuf buffer, StorageArrayRecipe recipe) {
		try {
			RecipeSerializer.SHAPELESS.write(buffer, recipe.getRecipe());
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error writing storage array recipe to packet.", e);
			throw e;
		}
	}
}
