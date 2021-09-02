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
	public StorageArrayRecipe read(ResourceLocation recipeId, JsonObject json) {
		return new StorageArrayRecipe((ShapelessRecipe) IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, json));
	}

	@Override
	public StorageArrayRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		try {
			return new StorageArrayRecipe((ShapelessRecipe) IRecipeSerializer.CRAFTING_SHAPELESS.read(recipeId, buffer));
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error reading storage array recipe from packet.", e);
			throw e;
		}
	}

	@Override
	public void write(PacketBuffer buffer, StorageArrayRecipe recipe) {
		try {
			IRecipeSerializer.CRAFTING_SHAPELESS.write(buffer, recipe.getRecipe());
		} catch (Exception e) {
			EnergyControl.LOGGER.error("Error writing storage array recipe to packet.", e);
			throw e;
		}
	}

}
