package com.zuxelus.energycontrol.recipes;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class KitAssemblerSerializer implements RecipeSerializer<KitAssemblerRecipe> {
	// an ingredient object with an optional "count" next to "item"/"tag": { "item": "...", "count": 2 }
	// the count is decoded first because a field codec hands the whole object on to the ingredient codec
	private static final Codec<Pair<Integer, Ingredient>> COUNTED_INGREDIENT = Codec.pair(Codec.INT.optionalFieldOf("count", 1).codec(), Ingredient.DISALLOW_EMPTY_CODEC);

	private static final Codec<KitAssemblerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			COUNTED_INGREDIENT.fieldOf("input1").forGetter(recipe -> Pair.of(recipe.count1, recipe.input1)),
			COUNTED_INGREDIENT.fieldOf("input2").forGetter(recipe -> Pair.of(recipe.count2, recipe.input2)),
			COUNTED_INGREDIENT.fieldOf("input3").forGetter(recipe -> Pair.of(recipe.count3, recipe.input3)),
			ItemStack.RECIPE_RESULT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
			Codec.INT.optionalFieldOf("time", 300).forGetter(recipe -> recipe.time)
		).apply(instance, (input1, input2, input3, output, time) -> new KitAssemblerRecipe(
			input1.getSecond(), input1.getFirst(), input2.getSecond(), input2.getFirst(), input3.getSecond(), input3.getFirst(), output, time)));

	@Override
	public Codec<KitAssemblerRecipe> codec() {
		return CODEC;
	}

	@Override
	public KitAssemblerRecipe read(PacketByteBuf buffer) {
		Ingredient input1 = Ingredient.fromPacket(buffer);
		int count1 = buffer.readVarInt();
		Ingredient input2 = Ingredient.fromPacket(buffer);
		int count2 = buffer.readVarInt();
		Ingredient input3 = Ingredient.fromPacket(buffer);
		int count3 = buffer.readVarInt();
		ItemStack output = buffer.readItemStack();
		int time = buffer.readInt();
		return new KitAssemblerRecipe(input1, count1, input2, count2, input3, count3, output, time);
	}

	@Override
	public void write(PacketByteBuf buffer, KitAssemblerRecipe recipe) {
		recipe.input1.write(buffer);
		buffer.writeVarInt(recipe.count1);
		recipe.input2.write(buffer);
		buffer.writeVarInt(recipe.count2);
		recipe.input3.write(buffer);
		buffer.writeVarInt(recipe.count3);
		buffer.writeItemStack(recipe.output);
		buffer.writeInt(recipe.time);
	}
}
