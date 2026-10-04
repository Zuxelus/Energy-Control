package com.zuxelus.energycontrol.recipes;

import com.mojang.serialization.MapCodec;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;

public class ArrayRecipeSerializer implements RecipeSerializer<StorageArrayRecipe> {
	// a storage array recipe is a plain shapeless recipe with custom matching
	private static final MapCodec<StorageArrayRecipe> CODEC = RecipeSerializer.SHAPELESS.codec()
			.xmap(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);
	private static final PacketCodec<RegistryByteBuf, StorageArrayRecipe> PACKET_CODEC = RecipeSerializer.SHAPELESS.packetCodec()
			.xmap(StorageArrayRecipe::new, StorageArrayRecipe::getRecipe);

	@Override
	public MapCodec<StorageArrayRecipe> codec() {
		return CODEC;
	}

	@Override
	public PacketCodec<RegistryByteBuf, StorageArrayRecipe> packetCodec() {
		return PACKET_CODEC;
	}
}
