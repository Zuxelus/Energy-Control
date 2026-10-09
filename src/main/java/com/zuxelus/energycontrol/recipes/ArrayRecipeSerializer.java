package com.zuxelus.energycontrol.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class ArrayRecipeSerializer {
	// a storage array recipe is a plain shapeless recipe with custom matching
	private static final MapCodec<StorageArrayRecipe> CODEC = ShapelessRecipe.MAP_CODEC
			.xmap(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);
	private static final StreamCodec<RegistryFriendlyByteBuf, StorageArrayRecipe> PACKET_CODEC = ShapelessRecipe.STREAM_CODEC
			.map(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);

	// RecipeSerializer is a record since 26.1
	public static RecipeSerializer<StorageArrayRecipe> create() {
		return new RecipeSerializer<>(CODEC, PACKET_CODEC);
	}

	private ArrayRecipeSerializer() {}
}
