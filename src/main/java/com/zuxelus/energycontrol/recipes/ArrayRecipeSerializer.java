package com.zuxelus.energycontrol.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ArrayRecipeSerializer implements RecipeSerializer<StorageArrayRecipe> {
	// a storage array recipe is a plain shapeless recipe with custom matching
	private static final MapCodec<StorageArrayRecipe> CODEC = RecipeSerializer.SHAPELESS_RECIPE.codec()
			.xmap(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);
	private static final StreamCodec<RegistryFriendlyByteBuf, StorageArrayRecipe> PACKET_CODEC = RecipeSerializer.SHAPELESS_RECIPE.streamCodec()
			.map(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);

	public static RecipeSerializer<StorageArrayRecipe> create() {
		return new ArrayRecipeSerializer();
	}

	@Override
	public MapCodec<StorageArrayRecipe> codec() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, StorageArrayRecipe> streamCodec() {
		return PACKET_CODEC;
	}
}
