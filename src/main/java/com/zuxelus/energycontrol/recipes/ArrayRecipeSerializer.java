package com.zuxelus.energycontrol.recipes;

import com.google.gson.JsonObject;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ArrayRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<StorageArrayRecipe> {

	@Override
	public StorageArrayRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		return new StorageArrayRecipe((ShapelessRecipe) IRecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
	}

	@Override
	public StorageArrayRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		try {
			return new StorageArrayRecipe((ShapelessRecipe) IRecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer));
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error reading storage array recipe from packet.", e);
			throw e;
		}
	}

	@Override
	public void toNetwork(PacketBuffer buffer, StorageArrayRecipe recipe) {
		try {
			IRecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe.getRecipe());
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error writing storage array recipe to packet.", e);
			throw e;
		}
	}

}
