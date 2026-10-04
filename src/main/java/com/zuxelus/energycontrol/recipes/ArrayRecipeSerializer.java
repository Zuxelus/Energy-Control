package com.zuxelus.energycontrol.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.zuxelus.energycontrol.EnergyControl;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;

public class ArrayRecipeSerializer implements RecipeSerializer<StorageArrayRecipe> {
	// recipe codecs are dispatched by "type" and must stay map codecs, so the xmap is applied to the MapCodec
	private static final Codec<StorageArrayRecipe> CODEC = ((MapCodec.MapCodecCodec<ShapelessRecipe>) RecipeSerializer.SHAPELESS.codec()).codec()
			.xmap(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe).codec();

	@Override
	public Codec<StorageArrayRecipe> codec() {
		return CODEC;
	}

	@Override
	public StorageArrayRecipe read(PacketByteBuf buffer) {
		try {
			return new StorageArrayRecipe(RecipeSerializer.SHAPELESS.read(buffer));
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
